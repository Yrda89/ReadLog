package com.example.readlog

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.readlog.database.LibrosDatabase
import com.example.readlog.databinding.ItemLibroBinding
import com.example.readlog.database.entities.LibrosEntity
import com.example.readlog.database.entities.PuntuacionesEntity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Singleton

class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemLibroBinding.bind(view)
    private val room: LibrosDatabase = Room.databaseBuilder(
        itemView.context.applicationContext,
        LibrosDatabase::class.java,
        "libros"
    ).build()

    fun render(librosEntity: LibrosEntity, navigateToDetailActivity: (Int) -> Unit) {
        binding.tvTitulo.text = "Título: " + librosEntity.titulo
        binding.tvAutor.text = "Autor: " + librosEntity.autor

        CoroutineScope(Dispatchers.IO).launch {
            val categoria = room.getCategoriaDao().getCategoriaPorId(librosEntity.id_categoria)
            withContext(Dispatchers.Main) {
                binding.tvCategoria.text = "Categoría: " + (categoria)
            }
        }

        binding.tvEditorial.text = "Editorial: " + librosEntity.editorial
        Picasso.get()
            .load(librosEntity.imagen)
            .placeholder(R.drawable.read_log_portada)
            .error(R.drawable.read_log_portada)
            .into(binding.ivImagen)

        binding.root.setOnClickListener {
            navigateToDetailActivity(librosEntity.id_Libro)
        }
    }
}
