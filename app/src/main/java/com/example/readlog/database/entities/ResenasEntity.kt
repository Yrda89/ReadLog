package com.example.readlog.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.readlog.Puntuacion
import com.example.readlog.Resena

@Entity(tableName = "resenas_table", foreignKeys = [ForeignKey(entity = LibrosEntity::class, parentColumns = ["id_Libro"], childColumns = ["id_libro"],
    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE, deferred = false)])
data class ResenasEntity (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_resena") val id_resena: Int = 0,
    @ColumnInfo(name="id_libro") val id_libro : Int,
    @ColumnInfo(name="texto_resena") val texto_resena : String

)

fun Resena.toDataBase(): ResenasEntity{
    return ResenasEntity(
        id_libro = this.id_libro,
        texto_resena = this.resena
    )
}