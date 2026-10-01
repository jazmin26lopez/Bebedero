package com.example.bebedero.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
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
    onGranja: (Int) -> Unit
) {
    val estado by produceState<List<Granja>?>(initialValue = null, key1 = Unit) {
        value = viewModel.granjas()
    }

    PantallaBase(titulo = "Monitoreo de Bebederos", subtitulo = nombreRol(rol)) {
        TituloSeccion(
            "Seleccionar granja",
            "Selecciona la granja que deseas revisar."
        )
        val granjas = estado
        if (granjas == null) {
            CargandoIndicador()
        } else {
            granjas.forEach { granja ->
                ContadorGalpones(viewModel, granja) { conteo ->
                    TarjetaNavegacion(
                        titulo = granja.nombre,
                        subtitulo = conteo,
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
