package com.example.bebedero.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bebedero.model.EstadoTemperatura

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

/** Estructura comun de pantalla: barra superior con titulo/subtitulo + contenido. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBase(
    titulo: String,
    subtitulo: String,
    contenido: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text(titulo, fontWeight = FontWeight.Bold)
                    Text(subtitulo, style = MaterialTheme.typography.bodySmall)
                }
            }
        )
        Column(modifier = Modifier.padding(16.dp)) {
            contenido()
        }
    }
}

@Composable
fun TituloSeccion(titulo: String, descripcion: String? = null) {
    Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    if (descripcion != null) {
        Spacer(Modifier.height(4.dp))
        Text(descripcion, style = MaterialTheme.typography.bodyMedium)
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
        CircularProgressIndicator()
    }
}

@Composable
fun TarjetaNavegacion(titulo: String, subtitulo: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(subtitulo, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun TarjetaLinea(nombre: String, temperatura: Double, estado: EstadoTemperatura, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(nombre, fontWeight = FontWeight.Bold)
                Text("%.1f °C".format(temperatura), style = MaterialTheme.typography.bodyMedium)
            }
            EstadoChip(estado)
        }
    }
}

@Composable
fun CajaDetalle(filas: List<Pair<String, String>>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            filas.forEach { (etiqueta, valor) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(etiqueta, style = MaterialTheme.typography.bodyMedium)
                    Text(valor, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
