package com.example.readlog.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.readlog.Libro
import com.example.readlog.database.entities.LibrosEntity

@Dao

interface LibroDao {

    @Query("SELECT * FROM libros_table")
    suspend fun getAllLibros():List<LibrosEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(libro: Libro)


    @Query("SELECT * FROM libros_table WHERE titulo LIKE :query")
    suspend fun getLibroPorTitulo(query: String):List<LibrosEntity>

    @Query("SELECT * FROM libros_table WHERE id_libro = :id")
    suspend fun getLibroPorId(id:Int):LibrosEntity


}