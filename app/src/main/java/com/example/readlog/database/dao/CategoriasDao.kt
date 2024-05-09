package com.example.readlog.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.readlog.Categoria
import com.example.readlog.database.entities.CategoriasEntity
import com.example.readlog.database.entities.LibrosEntity

@Dao
interface CategoriasDao {

    @Query("SELECT COUNT(*) FROM categorias_table")
    suspend fun getCantidadCategorias(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarCategoria(categoria: CategoriasEntity)

    @Transaction
    suspend fun llenarTablaCategorias() {
        val cantidadCategorias = getCantidadCategorias()
        if (cantidadCategorias == 0) {
            val items = listOf(
                "Ficción",
                "No ficción",
                "Misterio",
                "Suspenso",
                "Ciencia ficción",
                "Fantasía",
                "Terror",
                "Romance",
                "Drama",
                "Biografía",
                "Historia",
                "Autoayuda",
                "Poesía",
                "Infantil",
                "Juvenil"
            )
            items.forEach { nombreCategoria ->
                val categoria = CategoriasEntity(categoria = nombreCategoria)
                insertarCategoria(categoria)
            }
        }
    }

    @Query("SELECT * FROM categorias_table WHERE categoria = :query")
    suspend fun getCategoriaPorNombre(query: String): CategoriasEntity?

    @Query("SELECT * FROM categorias_table WHERE id_categoria = :query")
    suspend fun getCategoriaPorId(query: Int): CategoriasEntity?

    @Query("SELECT * FROM categorias_table")
    suspend fun getAllCategorias(): List<CategoriasEntity>
}