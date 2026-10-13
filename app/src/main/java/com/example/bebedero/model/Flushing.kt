
package com.example.bebedero.model

/**
 * LineaBebedero "1" -- "0..*" Flushing
 * Usuario "1" -- "0..*" Flushing : registra.
 *
 * fotoUri almacena la referencia a la fotografía del registro.
 * Es opcional para conservar compatibilidad con registros anteriores.
 */
data class Flushing(
    val id: Int,
    val lineaId: Int,
    val usuarioId: Int,
    val fechaHora: String,
    val observacion: String,
    val fotoUri: String? = null
)
