package com.example.readlog.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.readlog.database.entities.LibrosEntity
import com.example.readlog.database.entities.PuntuacionesEntity

@Dao
interface PuntuacionesDao {

    @Query("SELECT puntuacion FROM puntuaciones_table WHERE id_libro = :query")
    suspend fun getPuntuacionPorId(query: Int): Int?

    @Query("SELECT * FROM puntuaciones_table")
    suspend fun getAllPuntuaciones(): List<PuntuacionesEntity>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertPuntuacion(puntuacion: PuntuacionesEntity)

         @Query("UPDATE puntuaciones_table SET puntuacion = :puntuacion WHERE id_libro = :idLibro")
        suspend fun actualizarPuntuacion(idLibro: Int, puntuacion: Int)

        @Transaction
        suspend fun insertPuntuacionWithDefaultValues() {
            insertPuntuacion(PuntuacionesEntity(id_libro = 2, puntuacion = 5))

        }

}

