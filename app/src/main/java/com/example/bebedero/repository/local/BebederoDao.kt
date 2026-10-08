package com.example.bebedero.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BebederoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarGranjas(granjas: List<GranjaEntity>)

    @Query("SELECT * FROM granjas_cache")
    suspend fun obtenerGranjas(): List<GranjaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarGalpones(galpones: List<GalponEntity>)

    @Query("SELECT * FROM galpones_cache WHERE granjaId = :granjaId")
    suspend fun obtenerGalpones(granjaId: Int): List<GalponEntity>

    @Query("SELECT * FROM galpones_cache WHERE id = :id")
    suspend fun obtenerGalpon(id: Int): GalponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarLineas(lineas: List<LineaBebederoEntity>)

    @Query("SELECT * FROM lineas_cache")
    suspend fun obtenerTodasLasLineas(): List<LineaBebederoEntity>

    @Query("SELECT * FROM lineas_cache WHERE galponId = :galponId")
    suspend fun obtenerLineas(galponId: Int): List<LineaBebederoEntity>

    @Query("SELECT * FROM lineas_cache WHERE id = :id")
    suspend fun obtenerLinea(id: Int): LineaBebederoEntity?
}
