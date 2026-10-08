
package com.example.bebedero.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bebedero.model.EstadoTemperatura
import com.example.bebedero.model.Flushing
import com.example.bebedero.model.Galpon
import com.example.bebedero.model.Granja
import com.example.bebedero.model.LineaBebedero

/**
 * Entidades Room utilizadas para almacenar información local.
 *
 * Las granjas, galpones y líneas se mantienen como caché
 * de los datos obtenidos desde la API simulada.
 *
 * Los registros de flushing se almacenarán localmente
 * para permitir su consulta posterior por el Supervisor.
 */

// ------------------------------------
// ENTIDAD: GRANJAS
// ------------------------------------

@Entity(tableName = "granjas_cache")
data class GranjaEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String
)

// ------------------------------------
// ENTIDAD: GALPONES
// ------------------------------------

@Entity(tableName = "galpones_cache")
data class GalponEntity(
    @PrimaryKey
    val id: Int,
    val granjaId: Int,
    val nombre: String
)

// ------------------------------------
// ENTIDAD: LÍNEAS DE BEBEDEROS
// ------------------------------------

@Entity(tableName = "lineas_cache")
data class LineaBebederoEntity(
    @PrimaryKey
    val id: Int,
    val galponId: Int,
    val nombre: String,
    val temperaturaActual: Double,
    val fechaActualizacion: String,
    val estado: String
)

// ------------------------------------
// ENTIDAD: REGISTROS DE FLUSHING
// HU-07
// ------------------------------------

@Entity(tableName = "flushings")
data class FlushingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val lineaId: Int,
    val usuarioId: Int,
    val fechaHora: String,
    val observacion: String
)

// ====================================
// CONVERSIONES: GRANJAS
// ====================================

fun Granja.aEntity() = GranjaEntity(
    id = id,
    nombre = nombre
)

fun GranjaEntity.aDominio() = Granja(
    id = id,
    nombre = nombre
)

// ====================================
// CONVERSIONES: GALPONES
// ====================================

fun Galpon.aEntity() = GalponEntity(
    id = id,
    granjaId = granjaId,
    nombre = nombre
)

fun GalponEntity.aDominio() = Galpon(
    id = id,
    granjaId = granjaId,
    nombre = nombre
)

// ====================================
// CONVERSIONES: LÍNEAS
// ====================================

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

// ====================================
// FLUSHING
// ====================================

fun Flushing.aEntity() = FlushingEntity(
    id = id,
    lineaId = lineaId,
    usuarioId = usuarioId,
    fechaHora = fechaHora,
    observacion = observacion
)

fun FlushingEntity.aDominio() = Flushing(
    id = id,
    lineaId = lineaId,
    usuarioId = usuarioId,
    fechaHora = fechaHora,
    observacion = observacion
)
