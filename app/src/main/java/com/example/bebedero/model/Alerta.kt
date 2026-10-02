package com.example.bebedero.model

/**
 * LineaBebedero "1" -- "0..*" Alerta, y Alerta --> EstadoTemperatura en el diagrama de clases.
 */
data class Alerta(
    val id: Int,
    val lineaId: Int,
    val fechaHora: String,
    val estado: EstadoTemperatura,
    val activa: Boolean
)
