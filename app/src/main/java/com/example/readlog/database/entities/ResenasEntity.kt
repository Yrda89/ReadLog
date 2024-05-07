package com.example.readlog.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "resenas_table", foreignKeys = [ForeignKey(entity = LibrosEntity::class, parentColumns = ["id_Libro"], childColumns = ["id_libro"],
    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE, deferred = false)])
data class ResenasEntity (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_resena") val id_resena: Int = 0,
    @ColumnInfo(name="id_libro") val id_libro : Int, // Aquí ajustamos el nombre de la columna
    @ColumnInfo(name="texto_resena") val texto_resena : String,
    @ColumnInfo(name="fecha_resena") val fecha_resena : String
)