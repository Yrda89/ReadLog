package com.example.readlog

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.readlog.databinding.ItemLibroBinding
import com.example.readlog.database.entities.LibrosEntity
import com.example.readlog.database.entities.PuntuacionesEntity
import com.squareup.picasso.Picasso

class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemLibroBinding.bind(view)

    fun bind(librosEntity: LibrosEntity) {
        binding.tvTitulo.text = librosEntity.titulo
        binding.tvAutor.text = librosEntity.autor
        binding.tvCategoria.text = librosEntity.id_categoria.toString()
        binding.tvEditorial.text = librosEntity.editorial
        Picasso.get().load(librosEntity.imagen).into(binding.ivImagen)


    }
}