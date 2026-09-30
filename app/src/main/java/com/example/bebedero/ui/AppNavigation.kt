package com.example.bebedero.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**
 * Esqueleto de navegacion (tarjeta 4, Bloque 1). Las rutas de consulta
 * (granjas/galpones/lineas/detalle) hoy solo muestran un marcador de posicion;
 * se completan en la tarjeta 5.
 */
object Rutas {
    const val INICIO = "inicio"
    const val GRANJAS = "granjas/{rol}"
    const val GALPONES = "galpones/{rol}/{granjaId}"
    const val LINEAS = "lineas/{rol}/{galponId}"
    const val DETALLE = "detalle/{rol}/{lineaId}"
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val rolArg = navArgument("rol") { type = NavType.StringType }
    val enteroArg = { nombre: String -> navArgument(nombre) { type = NavType.IntType } }

    NavHost(navController = nav, startDestination = Rutas.INICIO) {
        composable(Rutas.INICIO) {
            InicioScreen(
                onOperario = { nav.navigate("granjas/operario") },
                onSupervisor = { nav.navigate("granjas/supervisor") }
            )
        }

        composable(Rutas.GRANJAS, arguments = listOf(rolArg)) { entry ->
            val rol = entry.arguments?.getString("rol").orEmpty()
            PantallaPendiente("Granjas ($rol)")
        }

        composable(Rutas.GALPONES, arguments = listOf(rolArg, enteroArg("granjaId"))) {
            PantallaPendiente("Galpones")
        }

        composable(Rutas.LINEAS, arguments = listOf(rolArg, enteroArg("galponId"))) {
            PantallaPendiente("Líneas")
        }

        composable(Rutas.DETALLE, arguments = listOf(rolArg, enteroArg("lineaId"))) {
            PantallaPendiente("Detalle de línea")
        }
    }
}

@Composable
private fun PantallaPendiente(nombre: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("$nombre — pendiente (tarjeta 5)")
    }
}
