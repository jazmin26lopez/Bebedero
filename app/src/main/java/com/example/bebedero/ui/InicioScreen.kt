
package com.example.bebedero.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

private val AzulBebedero = Color(0xFF0866B2)

@Composable
fun InicioScreen(
    onOperario: () -> Unit,
    onSupervisor: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fotografía de fondo original.
        Image(
            painter = painterResource(R.drawable.fondo_galpon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa oscura para facilitar la lectura.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0A263E).copy(alpha = 0.30f),
                            Color(0xFF0A263E).copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = 28.dp,
                    vertical = 38.dp
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Encabezado.
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Color(0xFF125385).copy(alpha = 0.72f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💧",
                    fontSize = 40.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Monitoreo de Bebederos",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Control de temperatura del agua",
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(55.dp))

            // Botón Operario.
            BotonRol(
                titulo = "Operario",
                subtitulo = "Monitoreo y registro",
                onClick = onOperario
            )

            Spacer(Modifier.height(15.dp))

            // Botón Supervisor.
            BotonRol(
                titulo = "Supervisor",
                subtitulo = "Supervisión y seguimiento",
                onClick = onSupervisor
            )

            Spacer(Modifier.height(35.dp))

            Text(
                text = "Sistema de monitoreo\nGranjas avícolas",
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
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    val interaccion = remember {
        MutableInteractionSource()
    }

    val presionado by interaccion.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.96f else 1f,
        label = "AnimacionBotonRol"
    )

    val forma = RoundedCornerShape(16.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .scale(escala)
            .shadow(4.dp, forma)
            .clip(forma)
            .background(AzulBebedero)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.55f),
                shape = forma
            )
            .clickable(
                interactionSource = interaccion,
                indication = null,
                onClick = onClick
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = subtitulo,
            color = Color.White.copy(alpha = 0.90f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}
