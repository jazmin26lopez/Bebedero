
package com.example.bebedero

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BebederoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Mantiene el acceso a Room.
        instancia = this

        // Canales de notificación.
        crearCanalesNotificacion()

    }

    private fun crearCanalesNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val administrador =
                getSystemService(NotificationManager::class.java)

            val canalTemperatura = NotificationChannel(
                CANAL_TEMPERATURA,
                "Alertas de temperatura",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos de temperaturas fuera del rango normal"
            }

            val canalFlushing = NotificationChannel(
                CANAL_FLUSHING,
                "Registros de flushing",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Cantidad de flushings realizados durante el día"
            }

            administrador.createNotificationChannel(canalTemperatura)
            administrador.createNotificationChannel(canalFlushing)
        }
    }

    private fun programarRevisionTemperatura() {

        val solicitud =
            PeriodicWorkRequestBuilder<TemperaturaWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "revision_temperatura_bebedero",
                ExistingPeriodicWorkPolicy.KEEP,
                solicitud
            )
    }

    companion object {
        const val CANAL_TEMPERATURA = "alertas_temperatura"
        const val CANAL_FLUSHING = "registros_flushing"

        lateinit var instancia: BebederoApp
            private set
    }
}
