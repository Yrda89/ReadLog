package com.example.readlog.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.readlog.Libro

@Entity(tableName = "libros_table")
data class LibrosEntity (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_libro") val id_libro: Int = 0,
    @ColumnInfo(name="titulo") val titulo : String,
    @ColumnInfo(name="autor") val autor : String,
    @ColumnInfo(name="categoria") val categoria : String,
    @ColumnInfo(name="editorial") val editorial : String,
    @ColumnInfo(name="puntuacion") val puntuacion : Int,
    @ColumnInfo(name="paginas") val paginas : Int,
    @ColumnInfo(name="imagen") val imagen : String,
    @ColumnInfo(name="resena") val resena : String
)

fun Libro.toDatabase() = LibrosEntity(titulo = titulo, autor = autor, categoria = categoria, editorial = editorial, puntuacion = puntuacion,
                                        paginas = paginas, imagen = imagen, resena = resena)
