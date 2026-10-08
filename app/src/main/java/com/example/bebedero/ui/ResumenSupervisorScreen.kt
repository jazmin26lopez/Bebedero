
package com.example.bebedero.ui

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.viewmodel.ConsultaViewModel

@Composable
fun ResumenSupervisorScreen(
    onVerGranjas: () -> Unit,
    onVerEventosCriticos: () -> Unit,
    viewModel: ConsultaViewModel = viewModel()
) {
    // Consultar información general
    val granjas by produceState<List<Granja>?>(initialValue = null) {
        value = viewModel.granjas()
    }

    val lineas by produceState<List<LineaBebedero>?>(initialValue = null) {
        value = viewModel.todasLasLineas()
    }

    // Calcular indicadores
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

    val totalEventos = totalCriticos + totalAdvertencias

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
                text = "Estado actual de las granjas y líneas de bebederos.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (granjas == null || lineas == null) {
                CargandoIndicador()
            } else {

                // --------------------------------
                // RESUMEN GENERAL
                // --------------------------------
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaIndicador(
                        titulo = "Granjas",
                        cantidad = totalGranjas,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    TarjetaIndicador(
                        titulo = "Líneas monitoreadas",
                        cantidad = totalLineas,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }

                // --------------------------------
                // PRIORIDAD: EVENTOS CRÍTICOS
                // --------------------------------
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Eventos que requieren atención",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        if (totalEventos > 0) {

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TarjetaIndicador(
                                    titulo = "Críticos",
                                    cantidad = totalCriticos,
                                    color = Color(0xFFA62929),
                                    modifier = Modifier.weight(1f)
                                )

                                TarjetaIndicador(
                                    titulo = "Advertencias",
                                    cantidad = totalAdvertencias,
                                    color = Color(0xFF8A5A00),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Text(
                                text = "$totalEventos líneas requieren atención.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Button(
                                onClick = onVerEventosCriticos,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver eventos críticos")
                            }

                        } else {
                            Text(
                                text = "Sin eventos activos. Todas las líneas monitoreadas se encuentran en estado normal.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF17643B)
                            )
                        }
                    }
                }

                // --------------------------------
                // LÍNEAS EN ESTADO NORMAL
                // --------------------------------
                TarjetaIndicador(
                    titulo = "Líneas en estado normal",
                    cantidad = totalNormales,
                    color = Color(0xFF17643B),
                    modifier = Modifier.fillMaxWidth()
                )

                // --------------------------------
                // CONSULTA GENERAL DE GRANJAS
                // --------------------------------
                Text(
                    text = "Consulta general",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Consulte el estado de las granjas, galpones y líneas de bebederos.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(
                    onClick = onVerGranjas,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver granjas")
                }
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
