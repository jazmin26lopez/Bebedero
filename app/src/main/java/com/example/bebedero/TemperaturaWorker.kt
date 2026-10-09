
package com.example.bebedero

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bebedero.repository.BebederoRepository

class TemperaturaWorker(
    context: Context,
    parametros: WorkerParameters
) : CoroutineWorker(context, parametros) {

    override suspend fun doWork(): Result {
        return try {

            // RF-12: comprobar el rol seleccionado.
            val preferenciasRol = applicationContext.getSharedPreferences(
                "bebedero_configuracion",
                Context.MODE_PRIVATE
            )

            val rolActual = preferenciasRol.getString(
                "rol_actual",
                ""
            )

            // Solo el Operario recibe alertas de temperatura.
            if (rolActual != "operario") {
                Log.d(
                    "TemperaturaWorker",
                    "Revisión omitida: rol actual = $rolActual"
                )
                return Result.success()
            }

            val repository = BebederoRepository()
            val lineas = repository.obtenerTodasLasLineas()

            // Recuerda las alertas notificadas para evitar
            // repetirlas en cada revisión.
            val preferencias = applicationContext.getSharedPreferences(
                "alertas_temperatura",
                Context.MODE_PRIVATE
            )

            val alertasActuales = mutableSetOf<String>()

            lineas.forEach { linea ->

                val mediciones = repository.obtenerHistorial(linea.id)
                val ultimaMedicion = mediciones.lastOrNull()

                if (ultimaMedicion != null) {

                    val temperatura = ultimaMedicion.temperatura

                    val estado = when {
                        temperatura < 15.0 || temperatura > 30.0 ->
                            "CRÍTICO"

                        temperatura > 21.0 ->
                            "ADVERTENCIA"

                        else -> "NORMAL"
                    }

                    if (estado != "NORMAL") {

                        val clave = "${linea.id}_$estado"
                        alertasActuales.add(clave)

                        // Comprobar nuevamente el rol antes de notificar.
                        // Así evitamos enviar alertas si el usuario cambió
                        // a Supervisor durante la revisión.
                        val rolVigente = preferenciasRol.getString(
                            "rol_actual",
                            ""
                        )

                        if (rolVigente != "operario" || isStopped) {
                            return Result.success()
                        }

                        if (!preferencias.getBoolean(clave, false)) {

                            NotificacionesBebedero.notificarTemperatura(
                                context = applicationContext,
                                ubicacion = "Línea ${linea.id}",
                                temperatura = temperatura,
                                estado = estado
                            )

                            Log.d(
                                "TemperaturaWorker",
                                "Alerta enviada: Línea ${linea.id}, " +
                                        "$temperatura °C, $estado"
                            )
                        }
                    }
                }
            }

            // Actualizar únicamente las alertas que siguen activas.
            preferencias.edit().apply {
                clear()

                alertasActuales.forEach { clave ->
                    putBoolean(clave, true)
                }
            }.apply()

            Result.success()

        } catch (e: Exception) {

            Log.e(
                "TemperaturaWorker",
                "Error al revisar temperaturas",
                e
            )

            Result.retry()
        }
    }
}
