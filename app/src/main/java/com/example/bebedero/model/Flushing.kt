package com.example.bebedero.model

/**
 * LineaBebedero "1" -- "0..*" Flushing, y Usuario "1" -- "0..*" Flushing : registra.
 */
data class Flushing(
    val id: Int,
    val lineaId: Int,
    val usuarioId: Int,
    val fechaHora: String
)
