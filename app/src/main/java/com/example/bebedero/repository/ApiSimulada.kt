package com.example.bebedero.repository

import com.example.bebedero.model.Alerta
import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.model.MedicionTemperatura
import com.example.bebedero.model.RolUsuario
import com.example.bebedero.model.Usuario
import kotlinx.coroutines.delay

/**
 * Simula un servicio remoto (RF-11 / RNF-04): expone los mismos datos que expondria
 * una API real, pero con una pequenia demora (delay) para imitar la latencia de red,
 * y usando datos ficticios en memoria (RNF-05: el MVP no se conecta a sistemas reales).
 *
 * El Repository es el unico lugar de la app que conoce esta clase; el resto del codigo
 * (ViewModel, UI) solo conoce al Repository.
 */
class ApiSimulada {

    private val usuarios = listOf(
        Usuario(id = 1, nombre = "Operario Demo", rol = RolUsuario.OPERARIO),
        Usuario(id = 2, nombre = "Supervisor Demo", rol = RolUsuario.SUPERVISOR)
    )

    private val granjas = listOf(
        Granja(id = 1, nombre = "Granja 1"),
        Granja(id = 2, nombre = "Granja 2"),
        Granja(id = 3, nombre = "Granja 3")
    )

    private val galponesPorGranja = mapOf(1 to 3, 2 to 2, 3 to 4)

    private val galpones: List<Galpon> = buildList {
        var id = 1
        granjas.forEach { granja ->
            for (n in 1..galponesPorGranja.getValue(granja.id)) {
                add(Galpon(id = id++, granjaId = granja.id, nombre = "Galpón $n"))
            }
        }
    }

    // Excepciones al valor normal: galponId a numero de linea a temperatura.
    private val excepciones = mapOf(
        (2 to 3) to 31.5,
        (4 to 2) to 28.7,
        (6 to 1) to 29.2
    )

    private fun estadoDe(temperatura: Double): EstadoTemperatura = when {
        temperatura >= 31.0 -> EstadoTemperatura.CRITICO
        temperatura >= 28.0 -> EstadoTemperatura.ADVERTENCIA
        else -> EstadoTemperatura.NORMAL
    }

    private val lineas: List<LineaBebedero> = buildList {
        var id = 1
        galpones.forEach { galpon ->
            for (n in 1..3) {
                val normal = 24.5 + ((galpon.id * 3 + n) % 5) * 0.6
                val temperatura = excepciones[galpon.id to n] ?: normal
                add(
                    LineaBebedero(
                        id = id++,
                        galponId = galpon.id,
                        nombre = "Línea $n",
                        temperaturaActual = temperatura,
                        fechaActualizacion = "29/09/2026 16:30",
                        estado = estadoDe(temperatura)
                    )
                )
            }
        }
    }

    private val alertas: List<Alerta> = lineas
        .filter { it.estado != EstadoTemperatura.NORMAL }
        .mapIndexed { index, linea ->
            Alerta(
                id = index + 1,
                lineaId = linea.id,
                fechaHora = linea.fechaActualizacion,
                estado = linea.estado,
                activa = true
            )
        }

    private val flushings = mutableListOf(
        Flushing(id = 1, lineaId = 6, usuarioId = 1, fechaHora = "29/09/2026 16:10"),
        Flushing(id = 2, lineaId = 12, usuarioId = 1, fechaHora = "29/09/2026 13:40")
    )

    // Simula la latencia de una llamada de red real.
    private suspend fun latenciaRed() = delay(400)

    suspend fun obtenerGranjas(): List<Granja> {
        latenciaRed()
        return granjas
    }

    suspend fun obtenerGalpones(granjaId: Int): List<Galpon> {
        latenciaRed()
        return galpones.filter { it.granjaId == granjaId }
    }

    suspend fun obtenerLineas(galponId: Int): List<LineaBebedero> {
        latenciaRed()
        return lineas.filter { it.galponId == galponId }
    }

    suspend fun obtenerTodasLasLineas(): List<LineaBebedero> {
        latenciaRed()
        return lineas
    }

    suspend fun obtenerAlertasActivas(): List<Alerta> {
        latenciaRed()
        return alertas.filter { it.activa }
    }

    suspend fun obtenerHistorial(lineaId: Int): List<MedicionTemperatura> {
        latenciaRed()
        val linea = lineas.first { it.id == lineaId }
        val variaciones = when (linea.estado) {
            EstadoTemperatura.CRITICO -> listOf(0.0, -2.8, -5.1, -5.7)
            EstadoTemperatura.ADVERTENCIA -> listOf(0.0, -1.4, -2.2, -2.6)
            EstadoTemperatura.NORMAL -> listOf(0.0, -0.4, 0.2, -0.6)
        }
        val horas = listOf("16:30", "15:30", "14:30", "13:30")
        return variaciones.indices.map { i ->
            MedicionTemperatura(
                id = i + 1,
                lineaId = lineaId,
                temperatura = linea.temperaturaActual + variaciones[i],
                fechaHora = "29/09/2026 ${horas[i]}"
            )
        }
    }

    suspend fun obtenerFlushings(): List<Flushing> {
        latenciaRed()
        return flushings.toList()
    }

    suspend fun registrarFlushing(lineaId: Int, usuarioId: Int, fechaHora: String): Flushing {
        latenciaRed()
        val registro = Flushing(id = flushings.size + 1, lineaId = lineaId, usuarioId = usuarioId, fechaHora = fechaHora)
        flushings.add(0, registro)
        return registro
    }

    suspend fun obtenerUsuario(id: Int): Usuario {
        latenciaRed()
        return usuarios.first { it.id == id }
    }
}
