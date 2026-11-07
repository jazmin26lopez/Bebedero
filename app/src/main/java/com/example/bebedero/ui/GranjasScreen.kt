package com.example.bebedero.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.repository.remote.RetrofitClient
import com.example.bebedero.viewmodel.ConsultaViewModel

private fun nombreRol(rol: String) = if (rol == "operario") "Operario" else "Supervisor"

/**
 * Primera pantalla del flujo de consulta: lista las granjas disponibles.
 * Compartida por Operario y Supervisor (HU-01 / HU-04).
 */
@Composable
fun GranjasScreen(
    rol: String,
    viewModel: ConsultaViewModel = viewModel(),
    onBack: () -> Unit,
    onGranja: (Int) -> Unit,
    onVerAlertas: () -> Unit = {}
) {
    val estado by produceState<List<Granja>?>(initialValue = null, key1 = Unit) {
        value = viewModel.granjas()
    }

    // API externa (Open-Meteo): temperatura ambiente de la zona, para
    // comparar con la del agua. Independiente de granjas/galpones/lineas,
    // que siguen viniendo de ms-infraestructura — si el clima falla, no
    // interfiere con el resto de la pantalla.
    val clima by produceState<String?>(initialValue = null, key1 = Unit) {
        value = try {
            val respuesta = RetrofitClient.clima.obtenerClimaActual()
            "%.1f °C".format(respuesta.current.temperature_2m)
        } catch (e: Exception) {
            null
        }
    }

    PantallaBase(titulo = "Monitoreo de Bebederos", subtitulo = nombreRol(rol)) {
        TituloSeccion(
            "Seleccionar granja",
            "Selecciona la granja que deseas revisar."
        )

        if (clima != null) {
            Text(
                text = "🌤️ Temperatura ambiente de la zona: $clima",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (rol == "operario") {
            BotonAzulAnimado(
                texto = "🔔 Ver alertas activas",
                onClick = onVerAlertas,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        val granjas = estado
        if (granjas == null) {
            CargandoIndicador()
        } else {
            granjas.forEach { granja ->
                ContadorGalpones(viewModel, granja) { conteo ->
                    TarjetaNavegacion(
                        titulo = granja.nombre,
                        subtitulo = conteo,
                        icono = "🌾",
                        onClick = { onGranja(granja.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContadorGalpones(
    viewModel: ConsultaViewModel,
    granja: Granja,
    contenido: @Composable (String) -> Unit
) {
    val galpones by produceState<List<Galpon>?>(initialValue = null, key1 = granja.id) {
        value = viewModel.galpones(granja.id)
    }
    contenido(galpones?.let { "${it.size} galpones" } ?: "Cargando…")
}
