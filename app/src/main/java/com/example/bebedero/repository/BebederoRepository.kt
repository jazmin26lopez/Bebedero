package com.example.bebedero.repository

import com.example.bebedero.model.Alerta
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.model.MedicionTemperatura

/**
 * Unica fuente de datos para el ViewModel: hoy llama a [ApiSimulada], y mas adelante
 * (RF-13/RNF-03) va a agregar una cache local con Room sin que el ViewModel lo note.
 */
class BebederoRepository(
    private val api: ApiSimulada = ApiSimulada()
) {

    suspend fun obtenerGranjas(): List<Granja> =
        api.obtenerGranjas()

    suspend fun obtenerGranja(id: Int): Granja =
        api.obtenerGranjas().first { it.id == id }

    suspend fun obtenerGalpones(granjaId: Int): List<Galpon> =
        api.obtenerGalpones(granjaId)

    suspend fun obtenerGalpon(id: Int): Galpon =
        api.obtenerGalpon(id)

    suspend fun obtenerLineas(galponId: Int): List<LineaBebedero> =
        api.obtenerLineas(galponId)

    suspend fun obtenerLinea(id: Int): LineaBebedero =
        api.obtenerTodasLasLineas()
            .first { it.id == id }

    suspend fun obtenerTodasLasLineas(): List<LineaBebedero> =
        api.obtenerTodasLasLineas()

    suspend fun obtenerAlertasActivas(): List<Alerta> =
        api.obtenerAlertasActivas()

    suspend fun obtenerHistorial(
        lineaId: Int
    ): List<MedicionTemperatura> =
        api.obtenerHistorial(lineaId)

    suspend fun obtenerFlushings(): List<Flushing> =
        api.obtenerFlushings()

    suspend fun registrarFlushing(
        lineaId: Int,
        usuarioId: Int,
        fechaHora: String,
        observacion: String
    ): Flushing =
        api.registrarFlushing(
            lineaId = lineaId,
            usuarioId = usuarioId,
            fechaHora = fechaHora,
            observacion = observacion
        )
}