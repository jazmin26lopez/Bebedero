package com.example.bebedero.repository

import com.example.bebedero.model.Alerta
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.model.MedicionTemperatura
import com.example.bebedero.repository.local.BebederoDao
import com.example.bebedero.repository.local.BebederoDatabase
import com.example.bebedero.repository.local.aDominio
import com.example.bebedero.repository.local.aEntity
import com.example.bebedero.model.Usuario

/**
 * Unica fuente de datos para el ViewModel: llama a [ApiSimulada] y guarda lo
 * consultado en una cache local con Room (RF-13/RNF-03). Si la "red" falla,
 * devuelve la ultima informacion guardada en esa cache, sin que el ViewModel
 * note de donde vino el dato.
 */
class BebederoRepository(
    private val api: ApiSimulada = ApiSimulada(),
    private val dao: BebederoDao = BebederoDatabase.instancia.bebederoDao()
) {

    suspend fun obtenerGranjas(): List<Granja> =
        try {
            val granjas = api.obtenerGranjas()
            dao.guardarGranjas(granjas.map { it.aEntity() })
            granjas
        } catch (e: Exception) {
            dao.obtenerGranjas().map { it.aDominio() }
        }

    suspend fun obtenerGranja(id: Int): Granja =
        obtenerGranjas().first { it.id == id }

    suspend fun obtenerGalpones(granjaId: Int): List<Galpon> =
        try {
            val galpones = api.obtenerGalpones(granjaId)
            dao.guardarGalpones(galpones.map { it.aEntity() })
            galpones
        } catch (e: Exception) {
            dao.obtenerGalpones(granjaId).map { it.aDominio() }
        }

    suspend fun obtenerGalpon(id: Int): Galpon =
        try {
            val galpon = api.obtenerGalpon(id)
            dao.guardarGalpones(listOf(galpon.aEntity()))
            galpon
        } catch (e: Exception) {
            dao.obtenerGalpon(id)?.aDominio() ?: throw e
        }

    suspend fun obtenerLineas(galponId: Int): List<LineaBebedero> =
        try {
            val lineas = api.obtenerLineas(galponId)
            dao.guardarLineas(lineas.map { it.aEntity() })
            lineas
        } catch (e: Exception) {
            dao.obtenerLineas(galponId).map { it.aDominio() }
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
            dao.guardarLineas(lineas.map { it.aEntity() })
            lineas
        } catch (e: Exception) {
            dao.obtenerTodasLasLineas().map { it.aDominio() }
        }

    suspend fun obtenerAlertasActivas(): List<Alerta> =
        api.obtenerAlertasActivas()

    suspend fun obtenerHistorial(
        lineaId: Int
    ): List<MedicionTemperatura> =
        api.obtenerHistorial(lineaId)

    suspend fun obtenerFlushings(): List<Flushing> =
        dao.obtenerFlushings().map { it.aDominio() }
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
        val idGenerado = dao.guardarFlushing(
            nuevoFlushing.aEntity()
        )
        return nuevoFlushing.copy(id = idGenerado.toInt())
    }

    suspend fun obtenerUsuario(id: Int): Usuario =
        api.obtenerUsuario(id)

}