
package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.viewmodel.ConsultaViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.modifier.modifierLocalConsumer

@Composable
fun ResumenSupervisorScreen(
    onVerGranjas: () -> Unit,
    onVerEventosCriticos: () -> Unit,
    viewModel: ConsultaViewModel=viewModel()
) {
    val granjas by produceState<List<Granja>?>(
        initialValue = null
    ) {
        value = viewModel.granjas()
    }

    val lineas by produceState<List<LineaBebedero>?>(
        initialValue = null
    ) {
        value = viewModel.todasLasLineas()
    }

    val totalGranjas = granjas?.size ?: 0
    val totalLineas = lineas?.size ?: 0

    val totalNormales = lineas?.count {
        it.estado == EstadoTemperatura.NORMAL
    } ?: 0

    val totalAdvertencias = lineas?.count {
        it.estado == EstadoTemperatura.ADVERTENCIA
    } ?: 0

    val totalCriticos = lineas?.count {
        it.estado == EstadoTemperatura.CRITICO
    } ?: 0


    PantallaBase(
        titulo = "Monitoreo de Bebederos",
        subtitulo = "Supervisor"
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen general",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Estado general de las granjas y líneas de bebederos."
            )

            if (granjas == null || lineas == null) {

                CargandoIndicador()

            } else {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Granjas")
                            Text(
                                "$totalGranjas",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Líneas monitoreadas")
                            Text(
                                "$totalLineas",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaIndicador(
                        titulo = "Normal",
                        cantidad = totalNormales,
                        color = Color(0xFF17643B),
                        modifier = Modifier.weight(1f)
                    )

                    TarjetaIndicador(
                        titulo = "Advertencia",
                        cantidad = totalAdvertencias,
                        color = Color(0xFF8A5A00),
                        modifier = Modifier.weight(1f)
                    )
                }

                TarjetaIndicador(
                    titulo = "Crítico",
                    cantidad = totalCriticos,
                    color = Color(0xFFA62929),
                    modifier = Modifier.fillMaxWidth()
                )
            }



            Button(
                onClick = onVerGranjas,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver granjas")
            }

            Button(
                onClick = onVerEventosCriticos,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver eventos críticos")
            }

        }
    }
}


@Composable
private fun TarjetaIndicador(
    titulo: String,
    cantidad: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

