
package com.example.bebedero.repository.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class ClimaActual(
    val temperature_2m: Double
)

data class ClimaResponse(
    val current: ClimaActual
)

// API externa real (Open-Meteo, no es un microservicio propio): clima
// actual de la zona, sin necesitar API key.
interface ClimaApiService {

    @GET("v1/forecast")
    suspend fun obtenerClimaActual(
        @Query("latitude") latitude: Double = -33.45,
        @Query("longitude") longitude: Double = -70.66,
        @Query("current") current: String = "temperature_2m"
    ): ClimaResponse
}
