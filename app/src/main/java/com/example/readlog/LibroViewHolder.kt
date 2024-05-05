package com.example.readlog

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.readlog.databinding.ItemLibroBinding
import com.example.readlog.database.entities.LibrosEntity
import com.squareup.picasso.Picasso

class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemLibroBinding.bind(view)

    fun bind(librosEntity : LibrosEntity,navigateToDetailActivity: (Int) -> Unit){

        binding.tvTitulo.text = librosEntity.titulo
        binding.tvAutor.text = librosEntity.autor
        binding.tvCategoria.text = librosEntity.categoria
        binding.tvEditorial.text = librosEntity.editorial
        binding.tvPuntuacion.text = librosEntity.puntuacion.toString()
        Picasso.get().load(librosEntity.imagen).into(binding.ivImagen)
        binding.root.setOnClickListener {
            navigateToDetailActivity(librosEntity.id_libro)
        }
    }
}