package com.example.readlog.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.readlog.database.dao.CategoriasDao
import com.example.readlog.database.dao.LibroDao
import com.example.readlog.database.dao.PuntuacionesDao
import com.example.readlog.database.dao.ResenasDao
import com.example.readlog.database.entities.CategoriasEntity
import com.example.readlog.database.entities.LibrosEntity
import com.example.readlog.database.entities.PuntuacionesEntity
import com.example.readlog.database.entities.ResenasEntity
import javax.inject.Singleton

@Singleton
@Database(entities = [LibrosEntity::class, CategoriasEntity::class,PuntuacionesEntity::class,ResenasEntity::class], version = 3)
abstract class LibrosDatabase : RoomDatabase() {
    abstract fun getLibroDao(): LibroDao
    abstract fun getCategoriaDao() : CategoriasDao
    abstract fun getPuntuacionesDao(): PuntuacionesDao
    abstract fun  getResenasDao(): ResenasDao
}