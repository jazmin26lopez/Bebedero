
package com.example.bebedero.repository.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Usa 127.0.0.1 en vez de la clasica 10.0.2.2 porque nos apoyamos en
 * "adb reverse tcp:8081 tcp:8081": eso crea un tunel directo entre el
 * emulador y el computador a traves del propio cable/conexion ADB, sin
 * depender de la red virtual del emulador ni del firewall de Windows.
 *
 * IMPORTANTE: cada vez que se reinicia el emulador hay que volver a correr
 * ese comando "adb reverse" (se pierde al apagar el emulador). Si se prueba
 * en un celular fisico, se necesita "adb reverse" igual (con el celular
 * conectado por USB y depuracion habilitada), no la IP de red local.
 */
object RetrofitClient {

    private const val BASE_URL_INFRAESTRUCTURA = "http://127.0.0.1:8081/"

    // API externa real (no es nuestro microservicio): clima, sin API key.
    private const val BASE_URL_CLIMA = "https://api.open-meteo.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val infraestructura: InfraestructuraApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_INFRAESTRUCTURA)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InfraestructuraApiService::class.java)
    }

    val clima: ClimaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CLIMA)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClimaApiService::class.java)
    }
}
