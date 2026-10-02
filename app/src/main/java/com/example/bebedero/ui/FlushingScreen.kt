package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.Flushing
import com.example.bebedero.viewmodel.FlushingViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FlushingScreen(
    lineaId: Int,
    onRegistrado: (Flushing) -> Unit,
    onCancelar: () -> Unit,
    flushingViewModel: FlushingViewModel = viewModel()
) {
    var mostrarConfirmacion by remember {
        mutableStateOf(false)
    }

    var registrando by remember {
        mutableStateOf(false)
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()

    PantallaBase(
        titulo = "Monitoreo de Bebederos",
        subtitulo = "Operario"
    ) {

        TituloSeccion(
            titulo = "Registrar flushing",
            descripcion = "Registra la limpieza realizada en la línea seleccionada."
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Datos del registro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Línea: $lineaId"
                )

                Text(
                    text = "Usuario: Operario Demo"
                )

                Text(
                    text = "Fecha y hora: se registrará automáticamente"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (!mostrarConfirmacion) {

            Button(
                onClick = {
                    mostrarConfirmacion = true
                    mensajeError = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar flushing")
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }

        } else {

            Text(
                text = "¿Confirmas que deseas registrar el flushing?",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (registrando) {

                CircularProgressIndicator()

            } else {

                Button(
                    onClick = {

                        val fechaHora = SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                        ).format(Date())

                        registrando = true
                        mensajeError = null

                        scope.launch {

                            try {

                                val flushing =
                                    flushingViewModel.registrarFlushing(
                                        lineaId = lineaId,
                                        usuarioId = 1,
                                        fechaHora = fechaHora
                                    )

                                onRegistrado(flushing)

                            } catch (e: Exception) {

                                mensajeError =
                                    "No se pudo registrar el flushing."

                            } finally {

                                registrando = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirmar registro")
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Button(
                    onClick = {
                        mostrarConfirmacion = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }

            mensajeError?.let { mensaje ->

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}