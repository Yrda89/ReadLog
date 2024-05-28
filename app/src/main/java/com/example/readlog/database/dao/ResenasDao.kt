package com.example.readlog.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.readlog.database.entities.PuntuacionesEntity
import com.example.readlog.database.entities.ResenasEntity

@Dao
interface ResenasDao {



    @Query("SELECT texto_resena FROM resenas_table WHERE id_libro = :query")
    suspend fun getResenaPorId(query: Int): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResena(resena: ResenasEntity)

    @Query("UPDATE resenas_table SET texto_resena = :textoResena WHERE id_libro = :idLibro")
    suspend fun actualizarResena(idLibro: Int, textoResena: String)
    @Transaction
    suspend fun insertResenaWithDefaultValues() {
        insertResena(ResenasEntity(id_libro = 2, texto_resena = "sadasdsdasdadasasdasdasddadadadsds"))

    }
}