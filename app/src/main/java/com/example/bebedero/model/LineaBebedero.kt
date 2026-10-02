package com.example.bebedero.model

/**
 * galponId referencia al Galpon dueno (Galpon "1" *-- "1..*" LineaBebedero en el diagrama de clases).
 */
data class LineaBebedero(
    val id: Int,
    val galponId: Int,
    val nombre: String,
    val temperaturaActual: Double,
    val fechaActualizacion: String,
    val estado: EstadoTemperatura
)
