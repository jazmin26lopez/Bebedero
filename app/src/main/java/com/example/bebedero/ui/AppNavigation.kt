package com.example.bebedero.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.fillMaxSize
object Rutas {
    const val INICIO = "inicio"

    const val RESUMEN_SUPERVISOR = "resumenSupervisor"

    const val GRANJAS = "granjas/{rol}"
    const val GALPONES = "galpones/{rol}/{granjaId}"
    const val LINEAS = "lineas/{rol}/{galponId}"
    const val DETALLE = "detalle/{rol}/{lineaId}"

    // Rutas de Flushing
    const val FLUSHING = "flushing/{lineaId}"
    const val CONFIRMACION_FLUSHING =
        "confirmacionFlushing/{flushingId}/{lineaId}/{fechaHora}"
    const val HISTORIAL = "historial/{lineaId}"
    const val EVENTOS_CRITICOS = "eventosCriticos"

}

@Composable
fun AppNavigation() {

    val nav = rememberNavController()

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

        // -------------------------
        // INICIO
        // -------------------------

        composable(Rutas.INICIO) {

            InicioScreen(
                onOperario = {
                    nav.navigate("granjas/operario")
                },
                onSupervisor = {
                    nav.navigate(Rutas.RESUMEN_SUPERVISOR)
                }
            )
        }

        // -------------------------
        // GRANJAS
        // -------------------------

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
                    nav.navigate(
                        "galpones/$rol/$granjaId"
                    )
                }
            )
        }

        // -------------------------
        // GALPONES
        // -------------------------

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
                    nav.navigate(
                        "lineas/$rol/$galponId"
                    )
                }
            )
        }

        // -------------------------
        // LÍNEAS
        // -------------------------

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
                    nav.navigate(
                        "detalle/$rol/$lineaId"
                    )
                }
            )
        }

        // -------------------------
        // DETALLE DE LÍNEA
        // -------------------------

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
                    nav.navigate(
                        "flushing/$lineaId"
                    )
                },
                onVerHistorial = {
                    nav.navigate("historial/$lineaId")
                }
            )
        }

        // -------------------------
        // HISTORIAL DE TEMPERATURAS
        // -------------------------

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


        // -------------------------
        // REGISTRAR FLUSHING
        // -------------------------

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

        // -------------------------
        // CONFIRMACIÓN FLUSHING
        // -------------------------

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

        // -------------------------
        // RESUMEN GENERAL SUPERVISOR
        // -------------------------

        composable(Rutas.RESUMEN_SUPERVISOR) {
            ResumenSupervisorScreen(
                onVerGranjas = {
                    nav.navigate("granjas/supervisor")
                },
                onVerEventosCriticos = {
                        nav.navigate(Rutas.EVENTOS_CRITICOS)
                }
            )
        }

        // -------------------------
        // EVENTOS CRÍTICOS - HU-05
        // -------------------------

        composable(Rutas.EVENTOS_CRITICOS) {
            EventosCriticosScreen(
                onVerDetalle = { lineaId ->
                    nav.navigate("detalle/supervisor/$lineaId")
                }
            )
        }


    }
}
@Composable
fun PantallaPendiente(
    titulo: String
) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize(),
        contentAlignment =
            androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "$titulo - En construcción"
        )
    }
}