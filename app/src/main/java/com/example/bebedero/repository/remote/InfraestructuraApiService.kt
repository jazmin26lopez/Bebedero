
package com.example.bebedero.repository.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class ActualizarTemperaturaRequest(
    val temperatura: Double,
    val fechaHora: String
)

/**
 * Espejo, lado app, de los endpoints REST expuestos por ms-infraestructura
 * (Spring Boot, puerto 8081). Reemplaza lo que antes resolvia ApiSimulada.
 */
interface InfraestructuraApiService {

    @GET("api/granjas")
    suspend fun obtenerGranjas(): List<GranjaDto>

    @GET("api/galpones")
    suspend fun obtenerGalpones(@Query("granjaId") granjaId: Long): List<GalponDto>

    @GET("api/galpones/{id}")
    suspend fun obtenerGalpon(@Path("id") id: Long): GalponDto

    @GET("api/lineas")
    suspend fun obtenerTodasLasLineas(): List<LineaBebederoDto>

    @GET("api/lineas")
    suspend fun obtenerLineas(@Query("galponId") galponId: Long): List<LineaBebederoDto>

    @PUT("api/lineas/{id}")
    suspend fun actualizarTemperatura(
        @Path("id") lineaId: Long,
        @Body body: ActualizarTemperaturaRequest
    ): LineaBebederoDto

    @GET("api/lineas/{id}/mediciones")
    suspend fun obtenerHistorial(@Path("id") lineaId: Long): List<MedicionTemperaturaDto>
}
