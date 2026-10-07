package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.viewmodel.ConsultaViewModel

/**
 * Ultima pantalla del flujo de consulta: muestra la temperatura y el estado
 * actual de una linea (HU-01 / HU-04). Desde aqui el Operario registra el
 * flushing (HU-03); esa pantalla la construyo Jazmin.
 */
@Composable
fun DetalleLineaScreen(
    rol: String,
    lineaId: Int,
    viewModel: ConsultaViewModel = viewModel(),
    onBack: () -> Unit,
    onRegistrarFlushing: () -> Unit
) {
    val linea by produceState<LineaBebedero?>(initialValue = null, key1 = lineaId) {
        value = viewModel.linea(lineaId)
    }
    val galpon by produceState<Galpon?>(initialValue = null, key1 = linea?.galponId) {
        value = linea?.let { viewModel.galpon(it.galponId) }
    }
    val granja by produceState<Granja?>(initialValue = null, key1 = galpon?.granjaId) {
        value = galpon?.let { viewModel.granja(it.granjaId) }
    }

    PantallaBase(
        titulo = linea?.nombre ?: "Línea",
        subtitulo = "${granja?.nombre.orEmpty()} · ${galpon?.nombre.orEmpty()}"
    ) {
        val actual = linea
        if (actual == null) {
            CargandoIndicador()
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Temperatura actual", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "%.1f °C".format(actual.temperaturaActual),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                EstadoChip(actual.estado)
                Text(
                    "Última actualización: ${actual.fechaActualizacion}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(modifier = Modifier.padding(top = 24.dp)) {
                CajaDetalle(
                    listOf(
                        "Granja" to (granja?.nombre ?: "-"),
                        "Galpón" to (galpon?.nombre ?: "-"),
                        "Línea" to actual.nombre,
                        "Estado" to actual.estado.etiqueta()
                    )
                )
            }

            if (rol == "operario") {
                Button(
                    onClick = onRegistrarFlushing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Registrar flushing")
                }
            }
            // Para Supervisor: el boton "Ver historial" lo agrega Jazmin cuando
            // construya esa pantalla (HU-06).
        }
    }
}
