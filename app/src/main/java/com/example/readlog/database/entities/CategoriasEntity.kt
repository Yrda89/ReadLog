package com.example.readlog.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.readlog.Categoria


@Entity(tableName = "categorias_table")
data class CategoriasEntity (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_categoria") val id_categoria: Int = 0,
    @ColumnInfo(name="categoria") val categoria : String

    )

fun Categoria.toDatabase(): CategoriasEntity {
    return CategoriasEntity(
        categoria = this.categoria
    )
}