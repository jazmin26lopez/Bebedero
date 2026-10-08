package com.example.bebedero.repository.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bebedero.BebederoApp

@Database(
    entities = [GranjaEntity::class, GalponEntity::class, LineaBebederoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BebederoDatabase : RoomDatabase() {

    abstract fun bebederoDao(): BebederoDao

    companion object {
        val instancia: BebederoDatabase by lazy {
            Room.databaseBuilder(
                BebederoApp.instancia,
                BebederoDatabase::class.java,
                "bebedero.db"
            ).build()
        }
    }
}
