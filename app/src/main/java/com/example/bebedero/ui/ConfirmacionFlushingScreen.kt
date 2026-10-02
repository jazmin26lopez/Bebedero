package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmacionFlushingScreen(
    flushingId: Int,
    lineaId: Int,
    fechaHora: String,
    onAceptar: () -> Unit
) {

    PantallaBase(
        titulo = "Monitoreo de Bebederos",
        subtitulo = "Operario"
    ) {

        TituloSeccion(
            titulo = "Flushing registrado",
            descripcion = "El registro se realizó correctamente."
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Registro exitoso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "N° de registro: $flushingId"
                )

                Text(
                    text = "Línea: $lineaId"
                )

                Text(
                    text = "Usuario: Operario Demo"
                )

                Text(
                    text = "Fecha y hora: $fechaHora"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onAceptar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Aceptar")
        }
    }
}