package com.example.readlog.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.readlog.Libro

@Entity(tableName = "libros_table", foreignKeys = [ForeignKey(entity = CategoriasEntity::class, parentColumns = ["id_categoria"], childColumns = ["id_categoria"],
    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE, deferred = false)])
data class LibrosEntity (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_Libro") val id_Libro: Int = 0,
    @ColumnInfo(name="titulo") val titulo : String,
    @ColumnInfo(name="autor") val autor : String,
    @ColumnInfo(name="id_categoria") val id_categoria : Int, // Clave foránea
    @ColumnInfo(name="editorial") val editorial : String,
    @ColumnInfo(name="paginas") val paginas : Int,
    @ColumnInfo(name="paginasLeidas") val paginasLeidas : Int,
    @ColumnInfo(name="imagen") val imagen : String,

)

fun Libro.toDatabase(): LibrosEntity {
    return LibrosEntity(
        titulo = this.titulo,
        autor = this.autor,
        id_categoria = this.id_categoria,
        editorial = this.editorial,
        paginas = this.paginas,
        paginasLeidas = this.paginasLeidas,
        imagen = this.imagen
    )
}
