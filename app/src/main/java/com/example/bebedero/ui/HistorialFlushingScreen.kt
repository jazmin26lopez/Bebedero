
package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.model.Usuario
import com.example.bebedero.viewmodel.ConsultaViewModel
import com.example.bebedero.viewmodel.FlushingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class PeriodoFlushing {
    DIARIO,
    MENSUAL,
    ANUAL
}

private val MESES = listOf(
    "Enero", "Febrero", "Marzo", "Abril",
    "Mayo", "Junio", "Julio", "Agosto",
    "Septiembre", "Octubre", "Noviembre", "Diciembre"
)

@Composable
fun HistorialFlushingScreen(
    flushingViewModel: FlushingViewModel = viewModel(),
    consultaViewModel: ConsultaViewModel = viewModel()
) {
    // Registros reales almacenados en Room.
    val flushings by produceState<List<Flushing>?>(null) {
        value = flushingViewModel.obtenerFlushings()
    }

    val granjas by produceState<List<Granja>?>(null) {
        value = consultaViewModel.granjas()
    }

    val galpones by produceState<List<Galpon>?>(null) {
        value = consultaViewModel.todosLosGalpones()
    }

    val lineas by produceState<List<LineaBebedero>?>(null) {
        value = consultaViewModel.todasLasLineas()
    }

    val usuarios by produceState<Map<Int, Usuario>?>(
        null,
        flushings
    ) {
        val ids = flushings?.map { it.usuarioId }?.distinct()
            ?: return@produceState

        value = ids.mapNotNull { id ->
            try {
                flushingViewModel.obtenerUsuario(id)
            } catch (_: Exception) {
                null
            }
        }.associateBy { it.id }
    }

    // Fecha actual del dispositivo.
    val hoy = Calendar.getInstance()
    val anioActual = hoy.get(Calendar.YEAR)
    val mesActual = hoy.get(Calendar.MONTH)
    val diaActual = hoy.get(Calendar.DAY_OF_MONTH)

    // Estado de los filtros.
    var periodo by remember {
        mutableStateOf(PeriodoFlushing.DIARIO)
    }

    var mesMensual by remember {
        mutableStateOf(mesActual)
    }

    var anioBusqueda by remember {
        mutableStateOf(anioActual.toString())
    }

    var mesBusqueda by remember {
        mutableStateOf(mesActual)
    }

    var diaBusqueda by remember {
        mutableStateOf(diaActual)
    }

    // Conversión de las fechas guardadas por los operarios.
    val formatoRegistro = remember {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale("es", "CL")
        ).apply {
            isLenient = false
        }
    }

    val anioHistorico = anioBusqueda.toIntOrNull()

    // Calcular los días válidos del mes seleccionado.
    val totalDiasMes = if (
        anioHistorico != null &&
        anioHistorico in 1..9999
    ) {
        Calendar.getInstance().apply {
            clear()
            set(anioHistorico, mesBusqueda, 1)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)
    } else {
        31
    }

    // Evitar fechas inexistentes.
    val diaValido = diaBusqueda.coerceIn(1, totalDiasMes)

    // Filtrar únicamente los registros del período solicitado.
    val registrosFiltrados = flushings?.filter { registro ->
        val fecha = try {
            formatoRegistro.parse(registro.fechaHora)
        } catch (_: Exception) {
            null
        }

        if (fecha == null) {
            false
        } else {
            val calendarioRegistro = Calendar.getInstance().apply {
                time = fecha
            }

            val anioRegistro =
                calendarioRegistro.get(Calendar.YEAR)

            val mesRegistro =
                calendarioRegistro.get(Calendar.MONTH)

            val diaRegistro =
                calendarioRegistro.get(Calendar.DAY_OF_MONTH)

            when (periodo) {
                PeriodoFlushing.DIARIO -> {
                    anioRegistro == anioActual &&
                            mesRegistro == mesActual &&
                            diaRegistro == diaActual
                }

                PeriodoFlushing.MENSUAL -> {
                    anioRegistro == anioActual &&
                            mesRegistro == mesMensual
                }

                PeriodoFlushing.ANUAL -> {
                    anioHistorico != null &&
                            anioRegistro == anioHistorico &&
                            mesRegistro == mesBusqueda &&
                            diaRegistro == diaValido
                }
            }
        }
    }?.sortedByDescending { registro ->
        try {
            formatoRegistro.parse(registro.fechaHora)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    val listaGranjas = granjas
    val listaGalpones = galpones
    val listaLineas = lineas
    val listaUsuarios = usuarios

    PantallaBase(
        titulo = "Historial de flushing",
        subtitulo = ""
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // FILTROS PRINCIPALES

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PeriodoFlushing.entries.forEach { opcion ->
                    val texto = when (opcion) {
                        PeriodoFlushing.DIARIO -> "Diario"
                        PeriodoFlushing.MENSUAL -> "Mensual"
                        PeriodoFlushing.ANUAL -> "Anual"
                    }

                    if (periodo == opcion) {
                        Button(
                            onClick = { periodo = opcion },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(texto)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { periodo = opcion },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(texto)
                        }
                    }
                }
            }

            // SELECCIÓN DEL PERÍODO

            when (periodo) {

                PeriodoFlushing.DIARIO -> {
                    Text(
                        text = "Registros de hoy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$diaActual de ${MESES[mesActual].lowercase()} de $anioActual",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                PeriodoFlushing.MENSUAL -> {
                    Text(
                        text = "Seleccione un mes de $anioActual",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    SelectorOpciones(
                        titulo = "Mes",
                        opciones = MESES,
                        seleccionado = mesMensual,
                        onSeleccionar = { mesMensual = it }
                    )
                }

                PeriodoFlushing.ANUAL -> {
                    Text(
                        text = "Búsqueda histórica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = anioBusqueda,
                        onValueChange = { nuevoValor ->
                            if (
                                nuevoValor.length <= 4 &&
                                nuevoValor.all { it.isDigit() }
                            ) {
                                anioBusqueda = nuevoValor
                            }
                        },
                        label = { Text("Año") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    SelectorOpciones(
                        titulo = "Mes",
                        opciones = MESES,
                        seleccionado = mesBusqueda,
                        onSeleccionar = {
                            mesBusqueda = it
                            diaBusqueda = 1
                        }
                    )

                    SelectorOpciones(
                        titulo = "Día",
                        opciones = (1..totalDiasMes).map {
                            it.toString()
                        },
                        seleccionado = diaValido - 1,
                        onSeleccionar = {
                            diaBusqueda = it + 1
                        }
                    )

                    if (
                        anioHistorico == null ||
                        anioHistorico !in 1..9999
                    ) {
                        Text(
                            text = "Ingrese un año válido de cuatro cifras.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // DATOS Y RESULTADOS

            if (
                flushings == null ||
                listaGranjas == null ||
                listaGalpones == null ||
                listaLineas == null ||
                listaUsuarios == null
            ) {
                CargandoIndicador()
            } else {

                val lista = registrosFiltrados.orEmpty()

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = when (periodo) {
                                PeriodoFlushing.DIARIO ->
                                    "Total de flushings realizados hoy"

                                PeriodoFlushing.MENSUAL ->
                                    "Total de flushings del mes seleccionado"

                                PeriodoFlushing.ANUAL ->
                                    "Total de flushings de la fecha seleccionada"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = lista.size.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (lista.isEmpty()) {
                    Text(
                        text = "No existen registros de flushing para el período seleccionado.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = lista,
                            key = { it.id }
                        ) { registro ->

                            // Ubicar la granja, galpón y línea.
                            val linea = listaLineas.find {
                                it.id == registro.lineaId
                            }

                            val galpon = listaGalpones.find {
                                it.id == linea?.galponId
                            }

                            val granja = listaGranjas.find {
                                it.id == galpon?.granjaId
                            }

                            val nombreOperario =
                                listaUsuarios[registro.usuarioId]?.nombre
                                    ?: "Operario no disponible"

                            TarjetaHistorialFlushing(
                                registro = registro,
                                granja = granja?.nombre
                                    ?: "Granja no disponible",
                                galpon = galpon?.nombre
                                    ?: "Galpón no disponible",
                                linea = linea?.nombre
                                    ?: "Línea no disponible",
                                operario = nombreOperario
                            )
                        }
                    }
                }
            }
        }
    }
}

// SELECTOR DE MESES O DÍAS

@Composable
private fun SelectorOpciones(
    titulo: String,
    opciones: List<String>,
    seleccionado: Int,
    onSeleccionar: (Int) -> Unit
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge
        )

        OutlinedButton(
            onClick = { expandido = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = opciones.getOrElse(seleccionado) {
                    "Seleccionar"
                }
            )
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEachIndexed { indice, opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionar(indice)
                        expandido = false
                    }
                )
            }
        }
    }
}

// DETALLE DE CADA REGISTRO

@Composable
private fun TarjetaHistorialFlushing(
    registro: Flushing,
    granja: String,
    galpon: String,
    linea: String,
    operario: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text(
                text = granja,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$galpon · $linea",
                style = MaterialTheme.typography.bodyLarge
            )

            HorizontalDivider()

            Text(
                text = "Fecha y hora: ${registro.fechaHora}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Operario: $operario",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Observaciones",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = registro.observacion,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
