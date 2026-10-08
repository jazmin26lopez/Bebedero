
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
    version = 2,
    exportSchema = false
)
abstract class BebederoDatabase : RoomDatabase() {

    abstract fun bebederoDao(): BebederoDao

    companion object {

        // Migración de la base de datos:
        // versión 1 a versión 2.
        // Conserva las tablas anteriores y agrega flushings.

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

        // Instancia única de la base de datos
        val instancia: BebederoDatabase by lazy {

            Room.databaseBuilder(
                BebederoApp.instancia,
                BebederoDatabase::class.java,
                "bebedero.db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}
