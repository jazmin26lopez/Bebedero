
package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
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

    val scope = rememberCoroutineScope()

    // Permite recargar los registros después de editar o eliminar.
    var revision by remember {
        mutableIntStateOf(0)
    }

    val flushings by produceState<List<Flushing>?>(
        initialValue = null,
        key1 = revision
    ) {
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
        initialValue = null,
        key1 = flushings
    ) {
        val ids = flushings?.map {
            it.usuarioId
        }?.distinct() ?: return@produceState

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

    // Filtros.
    var periodo by remember {
        mutableStateOf(PeriodoFlushing.DIARIO)
    }

    var mesMensual by remember {
        mutableIntStateOf(mesActual)
    }

    var anioBusqueda by remember {
        mutableStateOf(anioActual.toString())
    }

    var mesBusqueda by remember {
        mutableIntStateOf(mesActual)
    }

    var diaBusqueda by remember {
        mutableIntStateOf(diaActual)
    }

    // Estado de edición y eliminación.
    var registroEditar by remember {
        mutableStateOf<Flushing?>(null)
    }

    var registroEliminar by remember {
        mutableStateOf<Flushing?>(null)
    }

    var nuevaObservacion by remember {
        mutableStateOf("")
    }

    var procesando by remember {
        mutableStateOf(false)
    }

    var errorDialogo by remember {
        mutableStateOf<String?>(null)
    }

    var mensajeResultado by remember {
        mutableStateOf<String?>(null)
    }

    val formatoRegistro = remember {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale("es", "CL")
        ).apply {
            isLenient = false
        }
    }

    val anioHistorico = anioBusqueda.toIntOrNull()

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

    val diaValido = diaBusqueda.coerceIn(1, totalDiasMes)

    // Filtros originales: Diario, Mensual y Anual.
    val registrosFiltrados = flushings?.filter { registro ->

        val fecha = try {
            formatoRegistro.parse(registro.fechaHora)
        } catch (_: Exception) {
            null
        }

        if (fecha == null) {
            false
        } else {

            val calendario = Calendar.getInstance().apply {
                time = fecha
            }

            val anioRegistro = calendario.get(Calendar.YEAR)
            val mesRegistro = calendario.get(Calendar.MONTH)
            val diaRegistro = calendario.get(Calendar.DAY_OF_MONTH)

            when (periodo) {

                PeriodoFlushing.DIARIO ->
                    anioRegistro == anioActual &&
                            mesRegistro == mesActual &&
                            diaRegistro == diaActual

                PeriodoFlushing.MENSUAL ->
                    anioRegistro == anioActual &&
                            mesRegistro == mesMensual

                PeriodoFlushing.ANUAL ->
                    anioHistorico != null &&
                            anioRegistro == anioHistorico &&
                            mesRegistro == mesBusqueda &&
                            diaRegistro == diaValido
            }
        }
    }?.sortedByDescending { registro ->
        try {
            formatoRegistro.parse(registro.fechaHora)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    PantallaBase(
        titulo = "Historial de flushing",
        subtitulo = "Supervisor"
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // BOTONES DE FILTRO.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                PeriodoFlushing.entries.forEach { opcion ->

                    val texto = when (opcion) {
                        PeriodoFlushing.DIARIO -> "Diario"
                        PeriodoFlushing.MENSUAL -> "Mensual"
                        PeriodoFlushing.ANUAL -> "Anual"
                    }

                    BotonAzulAnimado(
                        texto = if (periodo == opcion) {
                            "✓ $texto"
                        } else {
                            texto
                        },
                        onClick = {
                            periodo = opcion
                            mensajeResultado = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // SELECCIÓN DEL PERÍODO.
            when (periodo) {

                PeriodoFlushing.DIARIO -> {

                    Text(
                        text = "Registros de hoy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$diaActual de ${MESES[mesActual].lowercase()} de $anioActual"
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
                        onSeleccionar = {
                            mesMensual = it
                        }
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
                        onValueChange = { valor ->
                            if (
                                valor.length <= 4 &&
                                valor.all { it.isDigit() }
                            ) {
                                anioBusqueda = valor
                            }
                        },
                        label = {
                            Text("Año")
                        },
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

            if (mensajeResultado != null) {
                Text(
                    text = mensajeResultado.orEmpty(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // RESULTADOS.
            val listaGranjas = granjas
            val listaGalpones = galpones
            val listaLineas = lineas
            val listaUsuarios = usuarios

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
                        text = "No existen registros de flushing para el período seleccionado."
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {

                        items(
                            items = lista,
                            key = { it.id }
                        ) { registro ->

                            val linea = listaLineas.find {
                                it.id == registro.lineaId
                            }

                            val galpon = listaGalpones.find {
                                it.id == linea?.galponId
                            }

                            val granja = listaGranjas.find {
                                it.id == galpon?.granjaId
                            }

                            val operario =
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
                                operario = operario,
                                onEditar = {

                                    nuevaObservacion = registro.observacion
                                    errorDialogo = null
                                    mensajeResultado = null
                                    registroEditar = registro
                                },
                                onEliminar = {

                                    errorDialogo = null
                                    mensajeResultado = null
                                    registroEliminar = registro
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // VENTANA DE EDICIÓN.
    val seleccionadoEditar = registroEditar

    if (seleccionadoEditar != null) {

        AlertDialog(
            onDismissRequest = {
                if (!procesando) {
                    registroEditar = null
                }
            },
            title = {
                Text("Editar observación")
            },
            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Registro N.º ${seleccionadoEditar.id}"
                    )

                    OutlinedTextField(
                        value = nuevaObservacion,
                        onValueChange = {
                            nuevaObservacion = it
                            errorDialogo = null
                        },
                        label = {
                            Text("Observación")
                        },
                        minLines = 3,
                        maxLines = 5,
                        enabled = !procesando,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorDialogo != null) {
                        Text(
                            text = errorDialogo.orEmpty(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {

                TextButton(
                    enabled = !procesando,
                    onClick = {

                        if (
                            !flushingViewModel.observacionValida(
                                nuevaObservacion
                            )
                        ) {
                            errorDialogo =
                                "La observación no puede estar vacía."
                        } else {

                            procesando = true
                            errorDialogo = null

                            scope.launch {
                                try {

                                    val actualizado =
                                        flushingViewModel
                                            .actualizarObservacionFlushing(
                                                flushingId =
                                                    seleccionadoEditar.id,
                                                observacion =
                                                    nuevaObservacion
                                            )

                                    if (actualizado) {
                                        revision++
                                        registroEditar = null
                                        mensajeResultado =
                                            "Observación actualizada correctamente."
                                    } else {
                                        errorDialogo =
                                            "No se encontró el registro."
                                    }

                                } catch (e: Exception) {
                                    errorDialogo =
                                        "No se pudo actualizar la observación."
                                } finally {
                                    procesando = false
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        if (procesando) "Guardando..." else "Guardar"
                    )
                }
            },
            dismissButton = {

                TextButton(
                    enabled = !procesando,
                    onClick = {
                        registroEditar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // VENTANA DE ELIMINACIÓN.
    val seleccionadoEliminar = registroEliminar

    if (seleccionadoEliminar != null) {

        AlertDialog(
            onDismissRequest = {
                if (!procesando) {
                    registroEliminar = null
                }
            },
            title = {
                Text("Eliminar registro")
            },
            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "¿Desea eliminar el registro N.º ${seleccionadoEliminar.id}?"
                    )

                    Text(
                        "Esta acción eliminará el registro de flushing y no se puede deshacer."
                    )

                    if (errorDialogo != null) {
                        Text(
                            text = errorDialogo.orEmpty(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {

                TextButton(
                    enabled = !procesando,
                    onClick = {

                        procesando = true
                        errorDialogo = null

                        scope.launch {
                            try {

                                val eliminado =
                                    flushingViewModel.eliminarFlushing(
                                        seleccionadoEliminar.id
                                    )

                                if (eliminado) {
                                    revision++
                                    registroEliminar = null
                                    mensajeResultado =
                                        "Registro eliminado correctamente."
                                } else {
                                    errorDialogo =
                                        "No se encontró el registro."
                                }

                            } catch (e: Exception) {
                                errorDialogo =
                                    "No se pudo eliminar el registro."
                            } finally {
                                procesando = false
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (procesando) {
                            "Eliminando..."
                        } else {
                            "Eliminar"
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {

                TextButton(
                    enabled = !procesando,
                    onClick = {
                        registroEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// SELECTOR DE MESES Y DÍAS.
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

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge
        )

        BotonAzulAnimado(
            texto = opciones.getOrElse(seleccionado) {
                "Seleccionar"
            },
            onClick = {
                expandido = true
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                expandido = false
            }
        ) {

            opciones.forEachIndexed { indice, opcion ->

                DropdownMenuItem(
                    text = {
                        Text(opcion)
                    },
                    onClick = {
                        onSeleccionar(indice)
                        expandido = false
                    }
                )
            }
        }
    }
}

// TARJETA DE CADA REGISTRO.
@Composable
private fun TarjetaHistorialFlushing(
    registro: Flushing,
    granja: String,
    galpon: String,
    linea: String,
    operario: String,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
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

            // BOTONES EXCLUSIVOS DEL SUPERVISOR.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                BotonAzulAnimado(
                    texto = "Editar",
                    onClick = onEditar,
                    modifier = Modifier.weight(1f)
                )

                OutlinedButton(
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
