package com.example.readlog.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias_table")
class CategoriasEntity (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_categoria") val id_categoria: Int = 0,
    @ColumnInfo(name="categoria") val categoria : String

    )