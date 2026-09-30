package com.example.bebedero.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bebedero.R

/**
 * Pantalla de inicio: el usuario elige su rol (Operario o Supervisor).
 * Mismo diseno que el prototipo HTML: foto del galpon de fondo + capa oscura
 * para que el texto sea legible.
 */
@Composable
fun InicioScreen(
    onOperario: () -> Unit,
    onSupervisor: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_galpon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A263E).copy(alpha = 0.30f), Color(0xFF0A263E).copy(alpha = 0.55f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 38.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF125385).copy(alpha = 0.72f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💧", fontSize = 40.sp)
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    "Monitoreo de Bebederos",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text("Control de temperatura del agua", color = Color.White.copy(alpha = 0.95f), fontSize = 15.sp)
            }
            Spacer(Modifier.height(55.dp))

            BotonRol(
                emoji = "👷",
                titulo = "Operario",
                subtitulo = "Monitoreo y registro",
                fondo = Color(0xFF124F7E).copy(alpha = 0.90f),
                colorTexto = Color.White,
                onClick = onOperario
            )
            Spacer(Modifier.height(15.dp))
            BotonRol(
                emoji = "👁️",
                titulo = "Supervisor",
                subtitulo = "Supervisión y seguimiento",
                fondo = Color(0xFFEFF4F7).copy(alpha = 0.94f),
                colorTexto = Color(0xFF173A5E),
                onClick = onSupervisor
            )

            Spacer(Modifier.height(35.dp))
            Text(
                "Sistema de monitoreo\nGranjas avícolas",
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BotonRol(
    emoji: String,
    titulo: String,
    subtitulo: String,
    fondo: Color,
    colorTexto: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .shadow(4.dp, RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .background(fondo)
            .border(1.dp, Color.White.copy(alpha = 0.80f), RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 34.sp, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
        Spacer(Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, color = colorTexto, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(subtitulo, color = colorTexto.copy(alpha = 0.90f), fontSize = 13.sp)
        }
        Text("›", color = colorTexto, fontSize = 30.sp)
    }
}
