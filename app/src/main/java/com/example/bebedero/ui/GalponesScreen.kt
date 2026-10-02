package com.example.bebedero.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.viewmodel.ConsultaViewModel

/**
 * Segunda pantalla del flujo de consulta: lista los galpones de una granja.
 * Compartida por Operario y Supervisor (HU-01 / HU-04).
 */
@Composable
fun GalponesScreen(
    granjaId: Int,
    viewModel: ConsultaViewModel = viewModel(),
    onBack: () -> Unit,
    onGalpon: (Int) -> Unit
) {
    val granja by produceState<Granja?>(initialValue = null, key1 = granjaId) {
        value = viewModel.granja(granjaId)
    }
    val galpones by produceState<List<Galpon>?>(initialValue = null, key1 = granjaId) {
        value = viewModel.galpones(granjaId)
    }

    PantallaBase(titulo = granja?.nombre ?: "Granja", subtitulo = "Seleccionar galpón") {
        TituloSeccion(
            "Seleccionar galpón",
            "Selecciona el galpón que deseas revisar."
        )
        val lista = galpones
        if (lista == null) {
            CargandoIndicador()
        } else {
            lista.forEach { galpon ->
                ContadorLineas(viewModel, galpon) { conteo ->
                    TarjetaNavegacion(
                        titulo = galpon.nombre,
                        subtitulo = conteo,
                        onClick = { onGalpon(galpon.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContadorLineas(
    viewModel: ConsultaViewModel,
    galpon: Galpon,
    contenido: @Composable (String) -> Unit
) {
    val lineas by produceState<List<LineaBebedero>?>(initialValue = null, key1 = galpon.id) {
        value = viewModel.lineas(galpon.id)
    }
    contenido(lineas?.let { "${it.size} líneas de bebederos" } ?: "Cargando…")
}
