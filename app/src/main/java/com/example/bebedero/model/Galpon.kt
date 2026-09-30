package com.example.bebedero.model

/**
 * granjaId referencia a la Granja duena (Granja "1" *-- "1..*" Galpon en el diagrama de clases).
 */
data class Galpon(
    val id: Int,
    val granjaId: Int,
    val nombre: String
)
