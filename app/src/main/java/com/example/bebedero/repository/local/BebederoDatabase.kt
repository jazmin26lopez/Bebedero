
package com.example.bebedero.repository.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bebedero.BebederoApp

@Database(
    entities = [
        GranjaEntity::class,
        GalponEntity::class,
        LineaBebederoEntity::class,
        FlushingEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BebederoDatabase : RoomDatabase() {

    abstract fun bebederoDao(): BebederoDao

    companion object {

        // Migración original: versión 1 a 2.
        // Se conserva para las instalaciones anteriores.
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `flushings` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `lineaId` INTEGER NOT NULL,
                        `usuarioId` INTEGER NOT NULL,
                        `fechaHora` TEXT NOT NULL,
                        `observacion` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        // Nueva migración: versión 2 a 3.
        // Agrega una columna opcional para la fotografía.
        // No borra los flushings existentes.
        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE `flushings`
                    ADD COLUMN `fotoUri` TEXT DEFAULT NULL
                    """.trimIndent()
                )
            }
        }

        // Instancia única de Room.
        val instancia: BebederoDatabase by lazy {

            Room.databaseBuilder(
                BebederoApp.instancia,
                BebederoDatabase::class.java,
                "bebedero.db"
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3
                )
                .build()
        }
    }
}
