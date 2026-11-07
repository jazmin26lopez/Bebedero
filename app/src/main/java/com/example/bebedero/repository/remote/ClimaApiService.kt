
package com.example.bebedero.repository.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API externa de clima (Open-Meteo, open-meteo.com): no requiere API key,
 * a diferencia de otros servicios de clima. Entrega la temperatura ambiente
 * actual para comparar con la del agua, sin interferir con los datos de
 * los microservicios propios.
 */
data class ClimaResponse(
    val current: ClimaActual
)

data class ClimaActual(
    val temperature_2m: Double,
    val time: String
)

interface ClimaApiService {

    @GET("v1/forecast")
    suspend fun obtenerClimaActual(
        @Query("latitude") latitud: Double = -33.4489,
        @Query("longitude") longitud: Double = -70.6693,
        @Query("current") parametro: String = "temperature_2m"
    ): ClimaResponse
}
