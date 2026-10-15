
package com.example.bebedero.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BebederoDao {

    // GRANJAS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarGranjas(granjas: List<GranjaEntity>)

    @Query("SELECT * FROM granjas_cache")
    suspend fun obtenerGranjas(): List<GranjaEntity>

    // GALPONES

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarGalpones(galpones: List<GalponEntity>)

    @Query("SELECT * FROM galpones_cache WHERE granjaId = :granjaId")
    suspend fun obtenerGalpones(granjaId: Int): List<GalponEntity>

    @Query("SELECT * FROM galpones_cache WHERE id = :id")
    suspend fun obtenerGalpon(id: Int): GalponEntity?

    // LÍNEAS DE BEBEDEROS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarLineas(lineas: List<LineaBebederoEntity>)

    @Query("SELECT * FROM lineas_cache")
    suspend fun obtenerTodasLasLineas(): List<LineaBebederoEntity>

    @Query("SELECT * FROM lineas_cache WHERE galponId = :galponId")
    suspend fun obtenerLineas(galponId: Int): List<LineaBebederoEntity>

    @Query("SELECT * FROM lineas_cache WHERE id = :id")
    suspend fun obtenerLinea(id: Int): LineaBebederoEntity?

    // REGISTROS DE FLUSHING - HU-07

    // CREATE: Registrar flushing
    @Insert
    suspend fun guardarFlushing(flushing: FlushingEntity): Long

    // READ: Consultar registros
    @Query("SELECT * FROM flushings ORDER BY id DESC")
    suspend fun obtenerFlushings(): List<FlushingEntity>

    // UPDATE: Editar únicamente la observación
    @Query("UPDATE flushings SET observacion = :observacion WHERE id = :flushingId")
    suspend fun actualizarObservacionFlushing(
        flushingId: Int,
        observacion: String
    ): Int

    // DELETE: Eliminar un registro por su identificador
    @Query("DELETE FROM flushings WHERE id = :flushingId")
    suspend fun eliminarFlushing(flushingId: Int): Int
}
