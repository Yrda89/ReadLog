package com.example.readlog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readlog.database.entities.LibrosEntity

class LibroAdapter(
    var libroList: List<LibrosEntity> = emptyList()) : RecyclerView.Adapter<LibroViewHolder>(){

    fun updateList(list: List<LibrosEntity>){
        libroList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        return LibroViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_libro, parent,false)
        )
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int){
        holder.bind(libroList[position])
    }

    override fun getItemCount() = libroList.size
}
