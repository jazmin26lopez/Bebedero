
package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * Detalle de una línea de bebederos.
 *
 * Operario:
 * - Consulta la temperatura.
 * - Registra flushing.
 *
 * Supervisor:
 * - Consulta la temperatura.
 * - Accede al historial de temperaturas.
 */
@Composable
fun DetalleLineaScreen(
    rol: String,
    lineaId: Int,
    viewModel: ConsultaViewModel = viewModel(),
    onBack: () -> Unit,
    onRegistrarFlushing: () -> Unit,
    onVerHistorial: () -> Unit
) {

    val linea by produceState<LineaBebedero?>(
        initialValue = null,
        key1 = lineaId
    ) {
        value = viewModel.linea(lineaId)
    }

    val galpon by produceState<Galpon?>(
        initialValue = null,
        key1 = linea?.galponId
    ) {
        value = linea?.let {
            viewModel.galpon(it.galponId)
        }
    }

    val granja by produceState<Granja?>(
        initialValue = null,
        key1 = galpon?.granjaId
    ) {
        value = galpon?.let {
            viewModel.granja(it.granjaId)
        }
    }

    PantallaBase(
        titulo = linea?.nombre ?: "Línea",
        subtitulo = "${granja?.nombre.orEmpty()} · ${galpon?.nombre.orEmpty()}"
    ) {

        val actual = linea

        if (actual == null) {

            CargandoIndicador()

        } else {

            // Contenido desplazable para teléfonos pequeños.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // TEMPERATURA ACTUAL
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Temperatura actual",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "%.1f °C".format(actual.temperaturaActual),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )

                    EstadoChip(actual.estado)

                    Text(
                        text = "Última actualización: ${actual.fechaActualizacion}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // INFORMACIÓN DE LA LÍNEA
                CajaDetalle(
                    filas = listOf(
                        "Granja" to (granja?.nombre ?: "-"),
                        "Galpón" to (galpon?.nombre ?: "-"),
                        "Línea" to actual.nombre,
                        "Estado" to actual.estado.etiqueta()
                    )
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                // ACCIONES SEGÚN EL PERFIL
                if (rol == "operario") {

                    BotonAzulAnimado(
                        texto = "Registrar flushing",
                        onClick = onRegistrarFlushing,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (rol == "supervisor") {

                    BotonAzulAnimado(
                        texto = "Ver historial de temperaturas",
                        onClick = onVerHistorial,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
