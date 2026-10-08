package com.example.bebedero.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero

/**
 * Copias en Room de los modelos de dominio, usadas unicamente como cache local
 * (RF-13/RNF-03) para poder mostrar la informacion mas reciente cuando la
 * conectividad es limitada o intermitente. El Repository es quien convierte
 * entre estas entidades y los modelos de dominio; el resto de la app no las conoce.
 */
@Entity(tableName = "granjas_cache")
data class GranjaEntity(
    @PrimaryKey val id: Int,
    val nombre: String
)

@Entity(tableName = "galpones_cache")
data class GalponEntity(
    @PrimaryKey val id: Int,
    val granjaId: Int,
    val nombre: String
)

@Entity(tableName = "lineas_cache")
data class LineaBebederoEntity(
    @PrimaryKey val id: Int,
    val galponId: Int,
    val nombre: String,
    val temperaturaActual: Double,
    val fechaActualizacion: String,
    val estado: String
)

fun Granja.aEntity() = GranjaEntity(id = id, nombre = nombre)
fun GranjaEntity.aDominio() = Granja(id = id, nombre = nombre)

fun Galpon.aEntity() = GalponEntity(id = id, granjaId = granjaId, nombre = nombre)
fun GalponEntity.aDominio() = Galpon(id = id, granjaId = granjaId, nombre = nombre)

fun LineaBebedero.aEntity() = LineaBebederoEntity(
    id = id,
    galponId = galponId,
    nombre = nombre,
    temperaturaActual = temperaturaActual,
    fechaActualizacion = fechaActualizacion,
    estado = estado.name
)

fun LineaBebederoEntity.aDominio() = LineaBebedero(
    id = id,
    galponId = galponId,
    nombre = nombre,
    temperaturaActual = temperaturaActual,
    fechaActualizacion = fechaActualizacion,
    estado = EstadoTemperatura.valueOf(estado)
)
