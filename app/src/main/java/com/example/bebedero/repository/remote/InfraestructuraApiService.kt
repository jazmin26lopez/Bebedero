
package com.example.bebedero.repository.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

data class GranjaDto(
    val id: Int,
    val nombre: String
)

data class GalponDto(
    val id: Int,
    val granjaId: Int,
    val nombre: String
)

data class LineaBebederoDto(
    val id: Int,
    val galponId: Int,
    val nombre: String,
    val temperaturaActual: Double,
    val fechaActualizacion: String,
    val estado: String
)

data class MedicionTemperaturaDto(
    val id: Int,
    val lineaId: Int,
    val temperatura: Double,
    val fechaHora: String
)

data class ActualizarTemperaturaRequest(
    val temperatura: Double,
    val fechaHora: String
)

interface InfraestructuraApiService {

    @GET("api/granjas")
    suspend fun obtenerGranjas(): List<GranjaDto>

    @GET("api/galpones")
    suspend fun obtenerGalpones(): List<GalponDto>

    @GET("api/lineas")
    suspend fun obtenerLineas(): List<LineaBebederoDto>

    @GET("api/lineas/{id}/mediciones")
    suspend fun obtenerHistorialTemperaturas(
        @Path("id") lineaId: Int
    ): List<MedicionTemperaturaDto>

    @PUT("api/lineas/{id}")
    suspend fun actualizarTemperatura(
        @Path("id") lineaId: Int,
        @Body request: ActualizarTemperaturaRequest
    ): LineaBebederoDto
}
