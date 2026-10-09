
package com.example.bebedero.repository

import android.content.Context
import android.util.Log
import com.example.bebedero.BebederoApp
import com.example.bebedero.NotificacionesBebedero
import com.example.bebedero.model.Alerta
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.model.MedicionTemperatura
import com.example.bebedero.model.Usuario
import com.example.bebedero.repository.local.BebederoDao
import com.example.bebedero.repository.local.BebederoDatabase
import com.example.bebedero.repository.local.aDominio
import com.example.bebedero.repository.local.aEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio principal de Bebedero.
 *
 * Conserva:
 * - API simulada.
 * - Almacenamiento local con Room.
 * - Consulta de granjas, galpones y líneas.
 * - Registro e historial de flushing.
 * - Notificaciones según el rol seleccionado.
 */
class BebederoRepository(
    private val api: ApiSimulada = ApiSimulada(),
    private val dao: BebederoDao =
        BebederoDatabase.instancia.bebederoDao()
) {

    suspend fun obtenerGranjas(): List<Granja> =
        try {
            val granjas = api.obtenerGranjas()

            dao.guardarGranjas(
                granjas.map { it.aEntity() }
            )

            granjas
        } catch (e: Exception) {
            dao.obtenerGranjas().map { it.aDominio() }
        }

    suspend fun obtenerGranja(id: Int): Granja =
        obtenerGranjas().first { it.id == id }

    suspend fun obtenerGalpones(
        granjaId: Int
    ): List<Galpon> =
        try {
            val galpones = api.obtenerGalpones(granjaId)

            dao.guardarGalpones(
                galpones.map { it.aEntity() }
            )

            galpones
        } catch (e: Exception) {
            dao.obtenerGalpones(granjaId)
                .map { it.aDominio() }
        }

    suspend fun obtenerGalpon(id: Int): Galpon =
        try {
            val galpon = api.obtenerGalpon(id)

            dao.guardarGalpones(
                listOf(galpon.aEntity())
            )

            galpon
        } catch (e: Exception) {
            dao.obtenerGalpon(id)?.aDominio() ?: throw e
        }

    suspend fun obtenerLineas(
        galponId: Int
    ): List<LineaBebedero> =
        try {
            val lineas = api.obtenerLineas(galponId)

            dao.guardarLineas(
                lineas.map { it.aEntity() }
            )

            lineas
        } catch (e: Exception) {
            dao.obtenerLineas(galponId)
                .map { it.aDominio() }
        }

    suspend fun obtenerLinea(id: Int): LineaBebedero =
        try {
            obtenerTodasLasLineas().first { it.id == id }
        } catch (e: NoSuchElementException) {
            dao.obtenerLinea(id)?.aDominio() ?: throw e
        }

    suspend fun obtenerTodasLasLineas(): List<LineaBebedero> =
        try {
            val lineas = api.obtenerTodasLasLineas()

            dao.guardarLineas(
                lineas.map { it.aEntity() }
            )

            lineas
        } catch (e: Exception) {
            dao.obtenerTodasLasLineas()
                .map { it.aDominio() }
        }

    suspend fun obtenerAlertasActivas(): List<Alerta> =
        api.obtenerAlertasActivas()

    suspend fun obtenerHistorial(
        lineaId: Int
    ): List<MedicionTemperatura> =
        api.obtenerHistorial(lineaId)

    // HISTORIAL REAL DE FLUSHING DESDE ROOM
    suspend fun obtenerFlushings(): List<Flushing> =
        dao.obtenerFlushings()
            .map { it.aDominio() }

    // REGISTRAR FLUSHING
    suspend fun registrarFlushing(
        lineaId: Int,
        usuarioId: Int,
        fechaHora: String,
        observacion: String
    ): Flushing {

        val nuevoFlushing = Flushing(
            id = 0,
            lineaId = lineaId,
            usuarioId = usuarioId,
            fechaHora = fechaHora,
            observacion = observacion
        )

        // Guardar en Room.
        val idGenerado = dao.guardarFlushing(
            nuevoFlushing.aEntity()
        )

        val flushingGuardado =
            nuevoFlushing.copy(
                id = idGenerado.toInt()
            )

        // RF-12: notificación exclusiva del Supervisor.
        try {
            val context = BebederoApp.instancia

            val preferencias = context.getSharedPreferences(
                "bebedero_configuracion",
                Context.MODE_PRIVATE
            )

            val rolActual = preferencias.getString(
                "rol_actual",
                ""
            )

            // No mostrar el contador al Operario.
            if (rolActual == "supervisor") {
                actualizarNotificacionFlushing()
            }

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "No se pudo actualizar la notificación de flushing",
                e
            )
        }

        return flushingGuardado
    }

    /**
     * Calcula la cantidad de flushings realizados hoy
     * a partir de los registros reales guardados en Room.
     *
     * Actualiza siempre la misma notificación.
     */
    suspend fun actualizarNotificacionFlushing() {

        val flushings = obtenerFlushings()

        val hoyIso = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())

        val hoyLatino = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())

        val hoyConGuiones = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        ).format(Date())

        val cantidadHoy = flushings.count { flushing ->

            val fecha = flushing.fechaHora.trim()

            fecha.startsWith(hoyIso) ||
                    fecha.startsWith(hoyLatino) ||
                    fecha.startsWith(hoyConGuiones)
        }

        NotificacionesBebedero.notificarFlushings(
            context = BebederoApp.instancia,
            cantidadHoy = cantidadHoy
        )
    }

    suspend fun obtenerUsuario(id: Int): Usuario =
        api.obtenerUsuario(id)
}
