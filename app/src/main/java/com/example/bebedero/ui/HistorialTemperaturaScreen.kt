
package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.MedicionTemperatura
import com.example.bebedero.viewmodel.ConsultaViewModel

@Composable
fun HistorialTemperaturaScreen(
    lineaId: Int,
    onBack: () -> Unit,
    viewModel: ConsultaViewModel = viewModel()
) {
    val linea by produceState(
        initialValue = "",
        key1 = lineaId
    ) {
        value = try {
            viewModel.linea(lineaId).nombre
        } catch (e: Exception) {
            "Línea $lineaId"
        }
    }

    val historial by produceState<List<MedicionTemperatura>?>(
        initialValue = null,
        key1 = lineaId
    ) {
        value = try {
            viewModel.historial(lineaId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    PantallaBase(
        titulo = "Historial de temperaturas",
        subtitulo = "Supervisor"
    ) {
        Column {
            TituloSeccion(
                titulo = linea,
                descripcion = "Registro histórico de temperaturas"
            )

            val mediciones = historial

            if (mediciones == null) {
                CargandoIndicador()
            } else if (mediciones.isEmpty()) {
                Text("No hay mediciones disponibles.")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(mediciones) { medicion ->
                        TarjetaMedicion(medicion)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al detalle")
            }
        }
    }
}

@Composable
private fun TarjetaMedicion(
    medicion: MedicionTemperatura
) {
    val estado = when {
        medicion.temperatura < 15.0 ||
                medicion.temperatura > 30.0 ->
            EstadoTemperatura.CRITICO

        medicion.temperatura > 21.0 ->
            EstadoTemperatura.ADVERTENCIA

        else -> EstadoTemperatura.NORMAL
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "%.1f °C".format(medicion.temperatura),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            EstadoChip(estado)

            Text(
                text = "Fecha y hora: ${medicion.fechaHora}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Línea ID: ${medicion.lineaId}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
