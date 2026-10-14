
package com.example.bebedero.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bebedero.model.EstadoTemperatura

// Color principal de los controles.
private val AzulBebederoComponentes = Color(0xFF0866B2)

fun EstadoTemperatura.etiqueta(): String = when (this) {
    EstadoTemperatura.NORMAL -> "Normal"
    EstadoTemperatura.ADVERTENCIA -> "Advertencia"
    EstadoTemperatura.CRITICO -> "Crítico"
}

fun EstadoTemperatura.colorTexto(): Color = when (this) {
    EstadoTemperatura.NORMAL -> Color(0xFF17643B)
    EstadoTemperatura.ADVERTENCIA -> Color(0xFF8A5A00)
    EstadoTemperatura.CRITICO -> Color(0xFFA62929)
}

fun EstadoTemperatura.colorFondo(): Color = when (this) {
    EstadoTemperatura.NORMAL -> Color(0xFFDCEFE4)
    EstadoTemperatura.ADVERTENCIA -> Color(0xFFFFF0CF)
    EstadoTemperatura.CRITICO -> Color(0xFFF8DDDD)
}

// Color vivo para la franja de acento de las tarjetas (más saturado que colorTexto,
// pensado para resaltar sobre el azul de TarjetaLinea/TarjetaNavegacion).
fun EstadoTemperatura.colorAcento(): Color = when (this) {
    EstadoTemperatura.NORMAL -> Color(0xFF2ECC71)
    EstadoTemperatura.ADVERTENCIA -> Color(0xFFF5A623)
    EstadoTemperatura.CRITICO -> Color(0xFFE74C3C)
}

@Composable
fun EstadoChip(estado: EstadoTemperatura) {
    Text(
        text = estado.etiqueta(),
        color = estado.colorTexto(),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(estado.colorFondo())
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

/**
 * Estructura común de las pantallas.
 * Conserva la barra superior y deja espacio
 * para la navegación inferior de Android.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBase(
    titulo: String,
    subtitulo: String,
    contenido: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold
                    )

                    if (subtitulo.isNotBlank()) {
                        Text(
                            text = subtitulo,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            contenido()
        }
    }
}

@Composable
fun TituloSeccion(
    titulo: String,
    descripcion: String? = null
) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )

    if (descripcion != null) {
        Spacer(Modifier.height(4.dp))

        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(Modifier.height(16.dp))
}

@Composable
fun CargandoIndicador() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = AzulBebederoComponentes
        )
    }
}

/**
 * Botón azul reutilizable.
 * Puede utilizarse en las demás pantallas.
 */
@Composable
fun BotonAzulAnimado(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    val interaccion = remember {
        MutableInteractionSource()
    }

    val presionado by interaccion.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.96f else 1f,
        label = "AnimacionBotonAzul"
    )

    Button(
        onClick = onClick,
        enabled = habilitado,
        interactionSource = interaccion,
        modifier = modifier.scale(escala),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzulBebederoComponentes,
            contentColor = Color.White
        )
    ) {
        Text(
            text = texto,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Tarjeta azul para navegar a otra pantalla.
 */
@Composable
fun TarjetaNavegacion(
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
    icono: String = ""
) {
    val interaccion = remember {
        MutableInteractionSource()
    }

    val presionado by interaccion.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.97f else 1f,
        label = "AnimacionTarjetaNavegacion"
    )

    val forma = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .scale(escala)
            .shadow(3.dp, forma)
            .clip(forma)
            .background(AzulBebederoComponentes)
            .clickable(
                interactionSource = interaccion,
                indication = null,
                onClick = onClick
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icono.isNotBlank()) {
                Text(
                    text = icono,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Text(
            text = subtitulo,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.90f)
        )
    }
}

/**
 * Tarjeta de una línea monitoreada.
 * Mismo estilo azul que TarjetaNavegacion; conserva el chip de estado
 * de temperatura (Normal/Advertencia/Crítico) para que siga destacando.
 */
@Composable
fun TarjetaLinea(
    nombre: String,
    temperatura: Double,
    estado: EstadoTemperatura,
    onClick: () -> Unit
) {
    val interaccion = remember {
        MutableInteractionSource()
    }

    val presionado by interaccion.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.98f else 1f,
        label = "AnimacionTarjetaLinea"
    )

    val forma = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .scale(escala)
            .shadow(3.dp, forma)
            .clip(forma)
            .background(AzulBebederoComponentes)
            .clickable(
                interactionSource = interaccion,
                indication = null,
                onClick = onClick
            )
            .height(IntrinsicSize.Min)
    ) {
        // Franja de acento: color vivo según el estado de la línea.
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(estado.colorAcento())
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💧 ",
                        color = Color.White
                    )

                    Text(
                        text = nombre,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "%.1f °C".format(temperatura),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.90f)
                )
            }

            EstadoChip(estado)
        }
    }
}

@Composable
fun CajaDetalle(
    filas: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            filas.forEach { (etiqueta, valor) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = etiqueta,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = valor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
