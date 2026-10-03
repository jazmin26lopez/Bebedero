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
 * Tercera pantalla del flujo de consulta: lista las lineas de bebederos de un galpon,
 * con su temperatura y estado actual. Compartida por Operario y Supervisor (HU-01 / HU-04).
 */
@Composable
fun LineasScreen(
    galponId: Int,
    viewModel: ConsultaViewModel = viewModel(),
    onBack: () -> Unit,
    onLinea: (Int) -> Unit
) {
    val galpon by produceState<Galpon?>(initialValue = null, key1 = galponId) {
        value = viewModel.galpon(galponId)
    }
    val granja by produceState<Granja?>(initialValue = null, key1 = galpon?.granjaId) {
        value = galpon?.let { viewModel.granja(it.granjaId) }
    }
    val lineas by produceState<List<LineaBebedero>?>(initialValue = null, key1 = galponId) {
        value = viewModel.lineas(galponId)
    }

    PantallaBase(titulo = galpon?.nombre ?: "Galpón", subtitulo = granja?.nombre ?: "") {
        TituloSeccion(
            "Líneas de bebederos",
            "Selecciona una línea para revisar su temperatura."
        )
        val lista = lineas
        if (lista == null) {
            CargandoIndicador()
        } else {
            lista.forEach { linea ->
                TarjetaLinea(
                    nombre = linea.nombre,
                    temperatura = linea.temperaturaActual,
                    estado = linea.estado,
                    onClick = { onLinea(linea.id) }
                )
            }
        }
    }
}
