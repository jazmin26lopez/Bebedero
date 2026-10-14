
package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.viewmodel.ConsultaViewModel

@Composable
fun EventosCriticosScreen(
    onVerDetalle: (Int) -> Unit,
    viewModel: ConsultaViewModel = viewModel()
) {
    ListaLineasConAtencion(
        titulo = "Eventos críticos",
        subtitulo = "Supervisor",
        onVerDetalle = onVerDetalle,
        viewModel = viewModel
    )
}

/**
 * Pantalla de alertas activas para el Operario (RF-07 / HU-02).
 * Muestra exactamente las mismas líneas que "Eventos críticos" (Advertencia/Crítico):
 * el caso define ambas historias de usuario sobre la misma información, solo que
 * una la consulta el Operario (HU-02) y la otra el Supervisor (HU-05).
 */
@Composable
fun AlertasActivasScreen(
    onVerDetalle: (Int) -> Unit,
    viewModel: ConsultaViewModel = viewModel()
) {
    ListaLineasConAtencion(
        titulo = "Alertas activas",
        subtitulo = "Operario",
        onVerDetalle = onVerDetalle,
        viewModel = viewModel
    )
}

@Composable
private fun ListaLineasConAtencion(
    titulo: String,
    subtitulo: String,
    onVerDetalle: (Int) -> Unit,
    viewModel: ConsultaViewModel
) {

    // Consultar líneas con alertas activas.
    val eventos by produceState<List<LineaBebedero>?>(
        initialValue = null
    ) {
        value = viewModel.eventosCriticos()
    }

    // Consultar granjas.
    val granjas by produceState<List<Granja>?>(
        initialValue = null
    ) {
        value = viewModel.granjas()
    }

    // Consultar galpones.
    val galpones by produceState<List<Galpon>?>(
        initialValue = null
    ) {
        value = viewModel.todosLosGalpones()
    }

    PantallaBase(
        titulo = titulo,
        subtitulo = subtitulo
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            TituloSeccion(
                titulo = "Líneas que requieren atención",
                descripcion = "Temperaturas en advertencia o estado crítico."
            )

            val lista = eventos
            val listaGranjas = granjas
            val listaGalpones = galpones

            if (
                lista == null ||
                listaGranjas == null ||
                listaGalpones == null
            ) {

                CargandoIndicador()

            } else if (lista.isEmpty()) {

                Text(
                    text = "No existen eventos activos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF17643B)
                )

            } else {

                val criticos = lista.count {
                    it.estado == EstadoTemperatura.CRITICO
                }

                val advertencias = lista.count {
                    it.estado == EstadoTemperatura.ADVERTENCIA
                }

                Text(
                    text = "${lista.size} líneas requieren atención",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$criticos críticas · $advertencias en advertencia",
                    style = MaterialTheme.typography.bodyMedium
                )

                // LISTADO DESPLAZABLE DE EVENTOS.
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        bottom = 24.dp
                    )
                ) {

                    items(
                        items = lista,
                        key = { it.id }
                    ) { linea ->

                        val galpon = listaGalpones.find {
                            it.id == linea.galponId
                        }

                        val granja = listaGranjas.find {
                            it.id == galpon?.granjaId
                        }

                        TarjetaEventoCritico(
                            linea = linea,
                            nombreGranja = granja?.nombre
                                ?: "Granja no disponible",
                            nombreGalpon = galpon?.nombre
                                ?: "Galpón no disponible",
                            onClick = {
                                onVerDetalle(linea.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaEventoCritico(
    linea: LineaBebedero,
    nombreGranja: String,
    nombreGalpon: String,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Nombre de la granja.
            Text(
                text = nombreGranja,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Galpón.
            Text(
                text = nombreGalpon,
                style = MaterialTheme.typography.titleSmall
            )

            // Línea monitoreada.
            Text(
                text = linea.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // Temperatura actual.
            Text(
                text = "%.1f °C".format(linea.temperaturaActual),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Conservamos los colores de alerta originales.
            EstadoChip(linea.estado)

            // Fecha de actualización.
            Text(
                text = "Última actualización: ${linea.fechaActualizacion}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Toca para ver el detalle",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
