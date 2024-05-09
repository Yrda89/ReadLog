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
    suspend fun getAllLibros(): List<LibrosEntity>

    @Query("SELECT id_Libro FROM libros_table")
    suspend fun  getIdLibro(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibro(libro: LibrosEntity)

    @Query("SELECT * FROM libros_table WHERE titulo LIKE :query")
    suspend fun getLibroPorTitulo(query: String): List<LibrosEntity>

    @Query("SELECT * FROM libros_table WHERE id_Libro = :id") // Cambiado a id_libro
    suspend fun getLibroPorId(id: Int): LibrosEntity

    @Query("DELETE FROM libros_table")
    suspend fun borrarLibros()

    @Query("DELETE FROM sqlite_sequence WHERE name LIKE 'libros_table'")
    suspend fun borrarPrimaryKey()

}