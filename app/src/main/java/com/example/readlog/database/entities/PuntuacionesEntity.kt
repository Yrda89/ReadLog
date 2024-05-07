package com.example.readlog.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "puntuaciones_table", foreignKeys = [ForeignKey(entity = LibrosEntity::class, parentColumns = ["id_Libro"], childColumns = ["id_libro"],
    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE, deferred = false)])
data class PuntuacionesEntity (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_puntuacion") val id_puntuacion: Int = 0,
    @ColumnInfo(name="id_libro") val id_libro : Int,
    @ColumnInfo(name="puntuacion") val puntuacion : String,
    @ColumnInfo(name="fecha_puntuacion") val fecha_puntuacion : String
)