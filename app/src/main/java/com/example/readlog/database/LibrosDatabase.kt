package com.example.readlog.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.readlog.database.dao.CategoriasDao
import com.example.readlog.database.dao.LibroDao
import com.example.readlog.database.entities.CategoriasEntity
import com.example.readlog.database.entities.LibrosEntity
import com.example.readlog.database.entities.PuntuacionesEntity
import com.example.readlog.database.entities.ResenasEntity

@Database(entities = [LibrosEntity::class, CategoriasEntity::class,PuntuacionesEntity::class,ResenasEntity::class], version = 1)
abstract class LibrosDatabase : RoomDatabase() {
    abstract fun getLibroDao(): LibroDao
    abstract fun getCategoriaDao() : CategoriasDao
}