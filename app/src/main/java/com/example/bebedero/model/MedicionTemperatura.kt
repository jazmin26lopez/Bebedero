package com.example.bebedero.model

/**
 * Historial de temperaturas de una linea (LineaBebedero "1" -- "0..*" MedicionTemperatura : historial).
 */
data class MedicionTemperatura(
    val id: Int,
    val lineaId: Int,
    val temperatura: Double,
    val fechaHora: String
)
