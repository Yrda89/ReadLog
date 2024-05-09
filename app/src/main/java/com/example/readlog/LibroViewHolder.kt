package com.example.readlog

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.readlog.database.LibrosDatabase
import com.example.readlog.databinding.ItemLibroBinding
import com.example.readlog.database.entities.LibrosEntity
import com.example.readlog.database.entities.PuntuacionesEntity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemLibroBinding.bind(view)
 //   private lateinit var room : LibrosDatabase

    fun render(librosEntity: LibrosEntity,navigateToDetailActivity: (Int) -> Unit) {
        binding.tvTitulo.text = "Título: "+librosEntity.titulo
        binding.tvAutor.text = "Autor: "+librosEntity.autor

/*        CoroutineScope(Dispatchers.IO).launch {
            val categoria = room.getCategoriaDao().getCategoriaPorId(librosEntity.id_categoria)
            withContext(Dispatchers.Main) {

                binding.tvCategoria.text = categoria.toString()
            }
        }
*/

        binding.tvEditorial.text = "Editorial: " + librosEntity.editorial
        Picasso.get().load(librosEntity.imagen).into(binding.ivImagen)
        binding.root.setOnClickListener {
            navigateToDetailActivity(librosEntity.id_Libro)
        }

    }
}