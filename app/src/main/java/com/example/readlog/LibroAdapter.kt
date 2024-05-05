package com.example.readlog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Type
import java.text.FieldPosition

class LibroAdapter(var libroList: List<Libro>,
                   private val navigateToDetailActivity: (Int) -> Unit) : RecyclerView.Adapter<LibroViewHolder>(){

    fun updateList(list: List<Libro>){
        libroList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        return LibroViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_libro, parent,false)
        )
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int){
        holder.bind(libroList[position],navigateToDetailActivity)
    }

    override fun getItemCount() = libroList.size
}
