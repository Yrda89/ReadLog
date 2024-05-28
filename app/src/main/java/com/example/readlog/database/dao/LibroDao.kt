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

    @Query("SELECT * FROM libros_table WHERE titulo = :query")
    suspend fun getLibroPorTitulo(query: String): LibrosEntity?

    @Query("SELECT * FROM libros_table WHERE titulo LIKE :query")
    suspend fun getLibrosPorTitulo(query: String): List<LibrosEntity>

    @Query("SELECT * FROM libros_table WHERE id_Libro = :id")
    suspend fun getLibroPorId(id: Int): LibrosEntity

    @Query("SELECT * FROM libros_table ORDER BY titulo ASC")
    suspend fun getLibrosOrdenTitulo(): List<LibrosEntity>

    @Query("SELECT * FROM libros_table ORDER BY autor ASC")
    suspend fun getAllLibrosPorAutor(): List<LibrosEntity>

    @Query("SELECT * FROM libros_table ORDER BY editorial ASC")
    suspend fun getAllLibrosPorEditorial(): List<LibrosEntity>

    @Query("SELECT libros_table.* FROM libros_table INNER JOIN categorias_table ON libros_table.id_categoria = categorias_table.id_categoria ORDER BY categorias_table.categoria ASC")
    suspend fun getAllLibrosPorCategoria(): List<LibrosEntity>


    @Query("DELETE FROM libros_table")
    suspend fun borrarLibros()

    @Query("DELETE FROM sqlite_sequence WHERE name LIKE 'libros_table'")
    suspend fun borrarPrimaryKey()

    @Query("DELETE FROM libros_table where id_Libro like :id")
    suspend fun borrarLibroPorId(id: Int)

    @Query("SELECT paginas from libros_table where id_Libro like :id")
    suspend fun getPaginasPorId(id:Int): Int

    @Query("SELECT paginasLeidas from libros_table where id_Libro like :id")
    suspend fun getPaginasLeidasPorId(id:Int): Int
}