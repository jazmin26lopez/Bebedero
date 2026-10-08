package com.example.bebedero

import android.app.Application

/**
 * Da acceso al Context de la aplicacion para que BebederoDatabase (Room) pueda
 * crearse sin que haya que pasar un Context a traves de cada ViewModel.
 */
class BebederoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instancia = this
    }

    companion object {
        lateinit var instancia: BebederoApp
            private set
    }
}
