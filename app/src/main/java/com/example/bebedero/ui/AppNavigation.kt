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
    const val GRANJAS = "granjas/{rol}"
    const val GALPONES = "galpones/{rol}/{granjaId}"
    const val LINEAS = "lineas/{rol}/{galponId}"
    const val DETALLE = "detalle/{rol}/{lineaId}"

    // Rutas de Flushing
    const val FLUSHING = "flushing/{lineaId}"
    const val CONFIRMACION_FLUSHING =
        "confirmacionFlushing/{flushingId}/{lineaId}/{fechaHora}"
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
                    nav.navigate("granjas/supervisor")
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
        ) {
            PantallaPendiente("Galpones")
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
        ) {
            PantallaPendiente("Líneas")
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
        ) {
            PantallaPendiente("Detalle de línea")
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