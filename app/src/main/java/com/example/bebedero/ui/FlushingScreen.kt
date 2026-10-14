
package com.example.bebedero.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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

    var observacion by remember {
        mutableStateOf("")
    }

    var errorObservacion by remember {
        mutableStateOf(false)
    }

    var mostrarConfirmacion by remember {
        mutableStateOf(false)
    }

    var registrando by remember {
        mutableStateOf(false)
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    // La fotografía se mantiene como vista previa temporal.
    var fotografia by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val scope = rememberCoroutineScope()
    val estadoScroll = rememberScrollState()

    // Cámara con fotografía de vista previa.
    val camara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen ->

        if (imagen != null) {
            fotografia = imagen
        }
    }

    PantallaBase(
        titulo = "Monitoreo de Bebederos",
        subtitulo = "Operario"
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(estadoScroll)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            TituloSeccion(
                titulo = "Registrar flushing",
                descripcion = "Registra la limpieza realizada en la línea seleccionada."
            )

            // DATOS DEL REGISTRO
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

                    Text("Línea: $lineaId")

                    Text("Usuario: Operario Demo")

                    Text(
                        "Fecha y hora: se registrará automáticamente"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (!mostrarConfirmacion) {

                // OBSERVACIÓN OBLIGATORIA
                Text(
                    text = "Observación *",
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = observacion,
                    onValueChange = {
                        observacion = it

                        if (flushingViewModel.observacionValida(it)) {
                            errorObservacion = false
                        }
                    },
                    label = {
                        Text("Ingrese una observación")
                    },
                    placeholder = {
                        Text(
                            "Ej: Limpieza realizada sin inconvenientes"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    isError = errorObservacion,
                    supportingText = {
                        if (errorObservacion) {
                            Text(
                                text = "Debe completar este campo",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // CÁMARA
                Text(
                    text = "Fotografía del flushing (opcional)",
                    fontWeight = FontWeight.Bold
                )

                BotonAzulAnimado(
                    texto = if (fotografia == null) {
                        "📷 Tomar fotografía"
                    } else {
                        "📷 Volver a tomar fotografía"
                    },
                    onClick = {
                        camara.launch(null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // VISTA PREVIA CON ANIMACIÓN
                AnimatedVisibility(
                    visible = fotografia != null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        fotografia?.let { imagen ->

                            Image(
                                bitmap = imagen.asImageBitmap(),
                                contentDescription =
                                    "Fotografía del flushing",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Fit
                            )

                            BotonAzulAnimado(
                                texto = "Quitar fotografía",
                                onClick = {
                                    fotografia = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // CONTINUAR AL REGISTRO
                BotonAzulAnimado(
                    texto = "Registrar flushing",
                    onClick = {

                        if (!flushingViewModel.observacionValida(observacion)) {
                            errorObservacion = true
                        } else {
                            errorObservacion = false
                            mensajeError = null
                            mostrarConfirmacion = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // CANCELAR
                BotonAzulAnimado(
                    texto = "Cancelar",
                    onClick = onCancelar,
                    modifier = Modifier.fillMaxWidth()
                )

            } else {

                // CONFIRMACIÓN
                Text(
                    text = "Confirmar registro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "¿Confirmas que deseas registrar el flushing?"
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text("Línea: $lineaId")

                        Text("Usuario: Operario Demo")

                        Text("Observación:")

                        Text(
                            text = observacion,
                            fontWeight = FontWeight.Bold
                        )

                        if (fotografia != null) {

                            Text(
                                "Fotografía tomada (vista previa)"
                            )

                            fotografia?.let { imagen ->

                                Image(
                                    bitmap = imagen.asImageBitmap(),
                                    contentDescription =
                                        "Vista previa de la fotografía",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                        } else {

                            Text("Fotografía: no adjuntada")
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                if (registrando) {

                    CircularProgressIndicator()

                } else {

                    BotonAzulAnimado(
                        texto = "Confirmar registro",
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
                                            fechaHora = fechaHora,
                                            observacion =
                                                observacion.trim()
                                        )

                                    onRegistrado(flushing)

                                } catch (e: Exception) {

                                    mensajeError =
                                        "No se pudo registrar el flushing. Intente nuevamente."

                                } finally {

                                    registrando = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    BotonAzulAnimado(
                        texto = "Volver",
                        onClick = {
                            mostrarConfirmacion = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                mensajeError?.let { mensaje ->

                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
