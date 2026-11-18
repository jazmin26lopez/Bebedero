
package com.example.bebedero.repository

import android.content.Context
import android.util.Log
import com.example.bebedero.BebederoApp
import com.example.bebedero.NotificacionesBebedero
import com.example.bebedero.model.Alerta
import com.example.bebedero.model.EstadoTemperatura
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
import com.example.bebedero.repository.remote.ActualizarFlushingRequest
import com.example.bebedero.repository.remote.ActualizarTemperaturaRequest
import com.example.bebedero.repository.remote.CrearFlushingRequest
import com.example.bebedero.repository.remote.RetrofitCliente
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio principal de Bebedero.
 *
 * ms-infraestructura:
 * - Granjas
 * - Galpones
 * - Lineas
 * - Temperaturas actuales
 * - Historial de mediciones
 *
 * ms-flushing:
 * - Consultar
 * - Registrar
 * - Editar observacion
 * - Eliminar
 *
 * Alertas:
 * - Calculadas desde los estados reales de las lineas.
 *
 * Room:
 * - Respaldo para consultas existentes.
 *
 * API simulada:
 * - Usuarios.
 * - Logica local de temperatura posterior al flushing,
 *   pendiente de reemplazar por una operacion real.
 */
class BebederoRepository(
    private val api: ApiSimulada = ApiSimulada(),
    private val dao: BebederoDao =
        BebederoDatabase.instancia.bebederoDao()
) {

    // ==========================================
    // GRANJAS
    // ==========================================

    suspend fun obtenerGranjas(): List<Granja> =
        try {
            val respuesta =
                RetrofitCliente.infraestructuraApi.obtenerGranjas()

            val granjas = respuesta.map { dto ->
                Granja(
                    id = dto.id,
                    nombre = dto.nombre
                )
            }

            dao.guardarGranjas(
                granjas.map { it.aEntity() }
            )

            granjas

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error consultando granjas desde Oracle",
                e
            )

            dao.obtenerGranjas()
                .map { it.aDominio() }
        }

    suspend fun obtenerGranja(id: Int): Granja =
        obtenerGranjas().first { it.id == id }

    // ==========================================
    // GALPONES
    // ==========================================

    suspend fun obtenerGalpones(
        granjaId: Int
    ): List<Galpon> =
        try {
            val respuesta =
                RetrofitCliente.infraestructuraApi.obtenerGalpones()

            val galpones = respuesta
                .filter { it.granjaId == granjaId }
                .map { dto ->
                    Galpon(
                        id = dto.id,
                        granjaId = dto.granjaId,
                        nombre = dto.nombre
                    )
                }

            dao.guardarGalpones(
                galpones.map { it.aEntity() }
            )

            galpones

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error consultando galpones desde Oracle",
                e
            )

            dao.obtenerGalpones(granjaId)
                .map { it.aDominio() }
        }

    suspend fun obtenerGalpon(id: Int): Galpon =
        try {
            val respuesta =
                RetrofitCliente.infraestructuraApi.obtenerGalpones()

            val dto = respuesta.first { it.id == id }

            val galpon = Galpon(
                id = dto.id,
                granjaId = dto.granjaId,
                nombre = dto.nombre
            )

            dao.guardarGalpones(
                listOf(galpon.aEntity())
            )

            galpon

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error consultando galpon $id desde Oracle",
                e
            )

            dao.obtenerGalpon(id)?.aDominio() ?: throw e
        }

    // ==========================================
    // LINEAS DE BEBEDEROS
    // ==========================================

    suspend fun obtenerLineas(
        galponId: Int
    ): List<LineaBebedero> =
        try {
            val respuesta =
                RetrofitCliente.infraestructuraApi.obtenerLineas()

            val lineas = respuesta
                .filter { it.galponId == galponId }
                .map { dto ->
                    LineaBebedero(
                        id = dto.id,
                        galponId = dto.galponId,
                        nombre = dto.nombre,
                        temperaturaActual = dto.temperaturaActual,
                        fechaActualizacion = dto.fechaActualizacion,
                        estado = EstadoTemperatura.valueOf(
                            dto.estado.uppercase(Locale.ROOT)
                        )
                    )
                }

            dao.guardarLineas(
                lineas.map { it.aEntity() }
            )

            lineas

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error consultando lineas desde Oracle",
                e
            )

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
            val respuesta =
                RetrofitCliente.infraestructuraApi.obtenerLineas()

            val lineas = respuesta.map { dto ->
                LineaBebedero(
                    id = dto.id,
                    galponId = dto.galponId,
                    nombre = dto.nombre,
                    temperaturaActual = dto.temperaturaActual,
                    fechaActualizacion = dto.fechaActualizacion,
                    estado = EstadoTemperatura.valueOf(
                        dto.estado.uppercase(Locale.ROOT)
                    )
                )
            }

            dao.guardarLineas(
                lineas.map { it.aEntity() }
            )

            lineas

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error consultando todas las lineas desde Oracle",
                e
            )

            dao.obtenerTodasLasLineas()
                .map { it.aDominio() }
        }

    // ==========================================
    // ALERTAS ACTIVAS
    // DATOS DE ORACLE
    // ==========================================

    suspend fun obtenerAlertasActivas(): List<Alerta> {

        val lineas = obtenerTodasLasLineas()

        return lineas
            .filter {
                it.estado != EstadoTemperatura.NORMAL
            }
            .mapIndexed { index, linea ->
                Alerta(
                    id = index + 1,
                    lineaId = linea.id,
                    fechaHora = linea.fechaActualizacion,
                    estado = linea.estado,
                    activa = true
                )
            }
    }

    // ==========================================
    // HISTORIAL DE TEMPERATURAS
    // ==========================================

    suspend fun obtenerHistorial(
        lineaId: Int
    ): List<MedicionTemperatura> {

        try {
            val respuesta =
                RetrofitCliente.infraestructuraApi
                    .obtenerHistorialTemperaturas(lineaId)

            return respuesta.map { dto ->
                MedicionTemperatura(
                    id = dto.id,
                    lineaId = dto.lineaId,
                    temperatura = dto.temperatura,
                    fechaHora = dto.fechaHora
                )
            }

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error consultando historial de linea $lineaId",
                e
            )

            throw e
        }
    }

    // ==========================================
    // FLUSHING - GET
    // ==========================================

    suspend fun obtenerFlushings(): List<Flushing> =
        try {
            val registros =
                RetrofitCliente.flushingApi.listar()

            registros.map { dto ->
                Flushing(
                    id = dto.id.toInt(),
                    lineaId = dto.lineaId.toInt(),
                    usuarioId = dto.usuarioId.toInt(),
                    fechaHora = dto.fechaHora,
                    observacion = dto.observacion ?: "",
                    fotoUri = dto.fotoUri
                )
            }

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error consultando flushings desde Oracle",
                e
            )

            dao.obtenerFlushings()
                .map { it.aDominio() }
        }

    // ==========================================
    // FLUSHING - POST
    // ==========================================

    suspend fun registrarFlushing(
        lineaId: Int,
        usuarioId: Int,
        fechaHora: String,
        observacion: String,
        fotoUri: String? = null
    ): Flushing {

        val request = CrearFlushingRequest(
            lineaId = lineaId.toLong(),
            usuarioId = usuarioId.toLong(),
            fechaHora = fechaHora,
            observacion = observacion,
            fotoUri = fotoUri
        )

        val respuesta =
            RetrofitCliente.flushingApi.crear(request)

        val flushingGuardado = Flushing(
            id = respuesta.id.toInt(),
            lineaId = respuesta.lineaId.toInt(),
            usuarioId = respuesta.usuarioId.toInt(),
            fechaHora = respuesta.fechaHora,
            observacion = respuesta.observacion ?: "",
            fotoUri = respuesta.fotoUri
        )

        // Al hacer flushing, la linea vuelve a una temperatura Normal.
        // Esto se refleja de verdad en ms-infraestructura; si el
        // microservicio no responde, el flushing igual queda guardado
        // (ver arriba), solo no se alcanza a resetear la linea.
        try {
            RetrofitCliente.infraestructuraApi.actualizarTemperatura(
                lineaId,
                ActualizarTemperaturaRequest(
                    temperatura = 18.0,
                    fechaHora = fechaHora
                )
            )
        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error reseteando temperatura de la linea $lineaId tras el flushing",
                e
            )
        }

        // RF-12: notificacion exclusiva del Supervisor.
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

            if (rolActual == "supervisor") {
                actualizarNotificacionFlushing()
            }

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error actualizando notificacion de flushing",
                e
            )
        }

        return flushingGuardado
    }

    // ==========================================
    // FLUSHING - PUT
    // ==========================================

    suspend fun actualizarObservacionFlushing(
        flushingId: Int,
        observacion: String
    ): Boolean {

        require(observacion.isNotBlank()) {
            "La observacion no puede estar vacia."
        }

        val request = ActualizarFlushingRequest(
            observacion = observacion.trim()
        )

        return try {
            RetrofitCliente.flushingApi.actualizar(
                id = flushingId.toLong(),
                request = request
            )

            true

        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                false
            } else {
                throw e
            }
        }
    }

    // ==========================================
    // FLUSHING - DELETE
    // ==========================================

    suspend fun eliminarFlushing(
        flushingId: Int
    ): Boolean {

        val respuesta =
            RetrofitCliente.flushingApi.eliminar(
                flushingId.toLong()
            )

        if (respuesta.code() == 404) {
            return false
        }

        if (!respuesta.isSuccessful) {
            throw retrofit2.HttpException(respuesta)
        }

        try {
            val context = BebederoApp.instancia

            val preferencias = context.getSharedPreferences(
                "bebedero_configuracion",
                Context.MODE_PRIVATE
            )

            if (
                preferencias.getString("rol_actual", "") ==
                "supervisor"
            ) {
                actualizarNotificacionFlushing()
            }

        } catch (e: Exception) {
            Log.e(
                "Bebedero",
                "Error actualizando contador de flushing",
                e
            )
        }

        return true
    }

    // ==========================================
    // NOTIFICACIONES
    // ==========================================

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

    // ==========================================
    // USUARIOS
    // ==========================================

    suspend fun obtenerUsuario(id: Int): Usuario =
        api.obtenerUsuario(id)
}
