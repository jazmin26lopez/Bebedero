package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Pantalla de inicio: el usuario elige su rol (Operario o Supervisor).
 * Segun el rol elegido, la navegacion decide que pantallas mostrar despues.
 */
@Composable
fun InicioScreen(
    onOperario: () -> Unit,
    onSupervisor: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Monitoreo de Bebederos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Control de temperatura del agua",
            style = MaterialTheme.typography.bodyMedium
        )

        Column(
            modifier = Modifier.padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onOperario) {
                Text("Entrar como Operario")
            }
            Button(onClick = onSupervisor) {
                Text("Entrar como Supervisor")
            }
        }
    }
}
