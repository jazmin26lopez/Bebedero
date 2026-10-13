
package com.example.bebedero

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bebedero.repository.ApiSimulada
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.TimeUnit

class BebederoApp : Application() {

    // Vive mientras la app esté viva: aquí corre la fluctuación simulada de temperaturas.
    private val scopeSimulacion = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // Mantiene el acceso a Room.
        instancia = this

        // Canales de notificación.
        crearCanalesNotificacion()

        // Simulación: las líneas cambian de temperatura solas con el tiempo,
        // para siempre tener eventos Crítico/Advertencia con los que probar.
        ApiSimulada.iniciarFluctuacionAutomatica(scopeSimulacion)

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
