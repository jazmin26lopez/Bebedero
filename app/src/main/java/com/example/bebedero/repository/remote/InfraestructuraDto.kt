
package com.example.bebedero.repository.remote

import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero
import com.example.bebedero.model.MedicionTemperatura

/**
 * DTOs que calzan con el JSON que entrega ms-infraestructura (Spring Boot).
 * Los id llegan como Long desde el backend; se convierten a Int para
 * seguir usando los mismos modelos de dominio (Granja, Galpon, etc.)
 * que ya tenia la app con Room/ApiSimulada.
 */

data class GranjaDto(
    val id: Long,
    val nombre: String
)

data class GalponDto(
    val id: Long,
    val granjaId: Long,
    val nombre: String
)

data class LineaBebederoDto(
    val id: Long,
    val galponId: Long,
    val nombre: String,
    val temperaturaActual: Double,
    val fechaActualizacion: String,
    val estado: String
)

data class MedicionTemperaturaDto(
    val id: Long,
    val lineaId: Long,
    val temperatura: Double,
    val fechaHora: String
)

fun GranjaDto.aDominio() = Granja(
    id = id.toInt(),
    nombre = nombre
)

fun GalponDto.aDominio() = Galpon(
    id = id.toInt(),
    granjaId = granjaId.toInt(),
    nombre = nombre
)

fun LineaBebederoDto.aDominio() = LineaBebedero(
    id = id.toInt(),
    galponId = galponId.toInt(),
    nombre = nombre,
    temperaturaActual = temperaturaActual,
    fechaActualizacion = fechaActualizacion,
    estado = EstadoTemperatura.valueOf(estado)
)

fun MedicionTemperaturaDto.aDominio() = MedicionTemperatura(
    id = id.toInt(),
    lineaId = lineaId.toInt(),
    temperatura = temperatura,
    fechaHora = fechaHora
)
