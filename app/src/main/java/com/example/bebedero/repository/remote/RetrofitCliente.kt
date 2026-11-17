
package com.example.bebedero.repository.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitCliente {

    // IP del computador donde se ejecuta Spring Boot.
    private const val IP_SERVIDOR = "10.57.65.63"

    // Cliente HTTP compartido por ambos microservicios.
    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    // ==========================================
    // MICROSERVICIO DE FLUSHING - PUERTO 8082
    // ==========================================

    private val retrofitFlushing: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://$IP_SERVIDOR:8082/")
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val flushingApi: FlushingApiService by lazy {
        retrofitFlushing.create(
            FlushingApiService::class.java
        )
    }

    // ==========================================
    // MICROSERVICIO DE INFRAESTRUCTURA - 8081
    // ==========================================

    private val retrofitInfraestructura: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://$IP_SERVIDOR:8081/")
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val infraestructuraApi: InfraestructuraApiService by lazy {
        retrofitInfraestructura.create(
            InfraestructuraApiService::class.java
        )
    }
}
