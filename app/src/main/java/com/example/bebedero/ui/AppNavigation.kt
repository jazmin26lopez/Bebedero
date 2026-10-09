
package com.example.bebedero.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bebedero.TemperaturaWorker
import com.example.bebedero.repository.BebederoRepository
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object Rutas {
    const val INICIO = "inicio"
    const val RESUMEN_SUPERVISOR = "resumenSupervisor"

    const val GRANJAS = "granjas/{rol}"
    const val GALPONES = "galpones/{rol}/{granjaId}"
    const val LINEAS = "lineas/{rol}/{galponId}"
    const val DETALLE = "detalle/{rol}/{lineaId}"

    const val FLUSHING = "flushing/{lineaId}"
    const val CONFIRMACION_FLUSHING =
        "confirmacionFlushing/{flushingId}/{lineaId}/{fechaHora}"

    const val HISTORIAL = "historial/{lineaId}"
    const val EVENTOS_CRITICOS = "eventosCriticos"
    const val HISTORIAL_FLUSHING = "historialFlushing"
}

@Composable
fun AppNavigation() {

    val nav = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val rolArg = navArgument("rol") {
        type = NavType.StringType
    }

    val enteroArg = { nombre: String ->
        navArgument(nombre) {
            type = NavType.IntType
        }
    }

    NavHost(
        navController = nav,
        startDestination = Rutas.INICIO
    ) {

        // INICIO
        composable(Rutas.INICIO) {
            InicioScreen(
                onOperario = {
                    configurarNotificacionesRol(
                        context = context,
                        rol = "operario"
                    )

                    nav.navigate("granjas/operario")
                },

                onSupervisor = {
                    configurarNotificacionesRol(
                        context = context,
                        rol = "supervisor"
                    )

                    nav.navigate(Rutas.RESUMEN_SUPERVISOR)

                    scope.launch {
                        try {
                            BebederoRepository()
                                .actualizarNotificacionFlushing()
                        } catch (e: Exception) {
                            Log.e(
                                "Bebedero",
                                "Error al actualizar contador de flushing",
                                e
                            )
                        }
                    }
                }
            )
        }

        // GRANJAS
        composable(
            route = Rutas.GRANJAS,
            arguments = listOf(rolArg)
        ) { entry ->

            val rol =
                entry.arguments?.getString("rol").orEmpty()

            GranjasScreen(
                rol = rol,
                onBack = {
                    nav.popBackStack()
                },
                onGranja = { granjaId ->
                    nav.navigate("galpones/$rol/$granjaId")
                }
            )
        }

        // GALPONES
        composable(
            route = Rutas.GALPONES,
            arguments = listOf(
                rolArg,
                enteroArg("granjaId")
            )
        ) { entry ->

            val rol =
                entry.arguments?.getString("rol").orEmpty()

            val granjaId =
                entry.arguments?.getInt("granjaId") ?: 0

            GalponesScreen(
                granjaId = granjaId,
                onBack = {
                    nav.popBackStack()
                },
                onGalpon = { galponId ->
                    nav.navigate("lineas/$rol/$galponId")
                }
            )
        }

        // LÍNEAS
        composable(
            route = Rutas.LINEAS,
            arguments = listOf(
                rolArg,
                enteroArg("galponId")
            )
        ) { entry ->

            val rol =
                entry.arguments?.getString("rol").orEmpty()

            val galponId =
                entry.arguments?.getInt("galponId") ?: 0

            LineasScreen(
                galponId = galponId,
                onBack = {
                    nav.popBackStack()
                },
                onLinea = { lineaId ->
                    nav.navigate("detalle/$rol/$lineaId")
                }
            )
        }

        // DETALLE DE LÍNEA
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(
                rolArg,
                enteroArg("lineaId")
            )
        ) { entry ->

            val rol =
                entry.arguments?.getString("rol").orEmpty()

            val lineaId =
                entry.arguments?.getInt("lineaId") ?: 0

            DetalleLineaScreen(
                rol = rol,
                lineaId = lineaId,
                onBack = {
                    nav.popBackStack()
                },
                onRegistrarFlushing = {
                    nav.navigate("flushing/$lineaId")
                },
                onVerHistorial = {
                    nav.navigate("historial/$lineaId")
                }
            )
        }

        // HISTORIAL DE TEMPERATURAS
        composable(
            route = Rutas.HISTORIAL,
            arguments = listOf(
                enteroArg("lineaId")
            )
        ) { entry ->

            val lineaId =
                entry.arguments?.getInt("lineaId") ?: 0

            HistorialTemperaturaScreen(
                lineaId = lineaId,
                onBack = {
                    nav.popBackStack()
                }
            )
        }

        // REGISTRAR FLUSHING
        composable(
            route = Rutas.FLUSHING,
            arguments = listOf(
                enteroArg("lineaId")
            )
        ) { entry ->

            val lineaId =
                entry.arguments?.getInt("lineaId") ?: 0

            FlushingScreen(
                lineaId = lineaId,
                onRegistrado = { flushing ->

                    val fechaCodificada =
                        Uri.encode(flushing.fechaHora)

                    nav.navigate(
                        "confirmacionFlushing/" +
                                "${flushing.id}/" +
                                "${flushing.lineaId}/" +
                                fechaCodificada
                    )
                },
                onCancelar = {
                    nav.popBackStack()
                }
            )
        }

        // CONFIRMACIÓN DE FLUSHING
        composable(
            route = Rutas.CONFIRMACION_FLUSHING,
            arguments = listOf(
                enteroArg("flushingId"),
                enteroArg("lineaId"),
                navArgument("fechaHora") {
                    type = NavType.StringType
                }
            )
        ) { entry ->

            val flushingId =
                entry.arguments?.getInt("flushingId") ?: 0

            val lineaId =
                entry.arguments?.getInt("lineaId") ?: 0

            val fechaHora =
                Uri.decode(
                    entry.arguments
                        ?.getString("fechaHora")
                        .orEmpty()
                )

            ConfirmacionFlushingScreen(
                flushingId = flushingId,
                lineaId = lineaId,
                fechaHora = fechaHora,
                onAceptar = {
                    nav.popBackStack(
                        route = Rutas.FLUSHING,
                        inclusive = true
                    )
                }
            )
        }

        // RESUMEN DEL SUPERVISOR
        composable(Rutas.RESUMEN_SUPERVISOR) {

            ResumenSupervisorScreen(
                onVerGranjas = {
                    nav.navigate("granjas/supervisor")
                },
                onVerEventosCriticos = {
                    nav.navigate(Rutas.EVENTOS_CRITICOS)
                },
                onVerHistorialFlushing = {
                    nav.navigate(Rutas.HISTORIAL_FLUSHING)
                }
            )
        }

        // EVENTOS CRÍTICOS
        composable(Rutas.EVENTOS_CRITICOS) {

            EventosCriticosScreen(
                onVerDetalle = { lineaId ->
                    nav.navigate("detalle/supervisor/$lineaId")
                }
            )
        }

        // HISTORIAL DE FLUSHING
        composable(Rutas.HISTORIAL_FLUSHING) {
            HistorialFlushingScreen()
        }
    }
}

// CONFIGURACIÓN DE NOTIFICACIONES SEGÚN ROL
private fun configurarNotificacionesRol(
    context: Context,
    rol: String
) {

    val preferencias = context.getSharedPreferences(
        "bebedero_configuracion",
        Context.MODE_PRIVATE
    )

    preferencias.edit()
        .putString("rol_actual", rol)
        .apply()

    val workManager = WorkManager.getInstance(context)

    if (rol == "operario") {

        val solicitud =
            PeriodicWorkRequestBuilder<TemperaturaWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        workManager.enqueueUniquePeriodicWork(
            "revision_temperatura_bebedero",
            ExistingPeriodicWorkPolicy.KEEP,
            solicitud
        )

    } else {

        workManager.cancelUniqueWork(
            "revision_temperatura_bebedero"
        )
    }
}

@Composable
fun PantallaPendiente(
    titulo: String
) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "$titulo - En construcción"
        )
    }
}
