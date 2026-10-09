
package com.example.bebedero

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificacionesBebedero {

    private const val ID_FLUSHING = 1001

    private fun tienePermiso(context: Context): Boolean {
        return Build.VERSION.SDK_INT < 33 ||
                context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    // SUPERVISOR: actualiza el contador diario.
    fun notificarFlushings(
        context: Context,
        cantidadHoy: Int
    ) {
        if (!tienePermiso(context)) return

        val mensaje = if (cantidadHoy == 1) {
            "1 flushing realizado hoy"
        } else {
            "$cantidadHoy flushings realizados hoy"
        }

        val notificacion = NotificationCompat.Builder(
            context,
            BebederoApp.CANAL_FLUSHING
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Bebedero · Registros de flushing")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(ID_FLUSHING, notificacion)
        } catch (e: SecurityException) {
            // Permiso denegado.
        }
    }

    // OPERARIO: alerta para una línea específica.
    fun notificarTemperatura(
        context: Context,
        ubicacion: String,
        temperatura: Double,
        estado: String
    ) {
        if (!tienePermiso(context)) return

        val mensajeTemperatura =
            String.format(
                java.util.Locale("es", "CL"),
                "%.1f",
                temperatura
            )

        val notificacion = NotificationCompat.Builder(
            context,
            BebederoApp.CANAL_TEMPERATURA
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Bebedero · Temperatura $estado")
            .setContentText("$ubicacion · $mensajeTemperatura °C")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "$ubicacion\n" +
                            "Temperatura: $mensajeTemperatura °C\n" +
                            "Estado: $estado"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Cada línea tiene su propia notificación.
        val idNotificacion = 2000 + ubicacion.hashCode()

        try {
            NotificationManagerCompat.from(context)
                .notify(idNotificacion, notificacion)
        } catch (e: SecurityException) {
            // Permiso denegado.
        }
    }
}
