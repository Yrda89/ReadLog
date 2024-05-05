package com.example.readlog.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "resenas_table", foreignKeys = [ForeignKey(entity = LibrosEntity::class, parentColumns = ["idLibro"], childColumns = ["id_libro"],
    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE, deferred = false)])
class PuntuacionesEntity (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_puntuacion") val id_puntuacion: Int = 0,
    @ColumnInfo(name="idLibro") val idLibro : String,
    @ColumnInfo(name="puntuacion") val puntuacion : String,
    @ColumnInfo(name="fecha_puntuacion") val fecha_puntuacion : String
)