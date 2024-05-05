package com.example.readlog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.readlog.database.LibrosDatabase
import com.example.readlog.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var binding: ActivityMainBinding
    private lateinit var addLibro: FloatingActionButton
    private lateinit var room : LibrosDatabase
    private lateinit var adapter: LibroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        room = Room.databaseBuilder(this, LibrosDatabase::class.java, "libros").build()
        binding.searchView.setOnClickListener{}
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        initListeners()
       // initUI()
        setUpAutoCompleteTextView()
    }

/*
    private fun initUI(){
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //searchByName(query)
                return false
            }
            override fun onQueryTextChange(newText: String?) = false
        })


    }
*/

    private fun initComponents() {
        addLibro = findViewById(R.id.addLibro)
    }

    private fun initListeners() { addLibro.setOnClickListener{ showDialog() }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_libro)
        val btnAddLibro: Button = dialog.findViewById(R.id.btnAddLibro)

        val titulo : EditText = dialog.findViewById(R.id.etTitulo)
        val autor : EditText = dialog.findViewById(R.id.etAutor)
        val editorial : EditText = dialog.findViewById(R.id.etEditorial)
        val puntuacion : EditText = dialog.findViewById(R.id.etPuntuacion)
        val paginas : EditText = dialog.findViewById(R.id.etPaginas)
        val imagen : EditText = dialog.findViewById(R.id.etImagen)
        val resena : String = ""



        // Configurar AutoCompleteTextView para categorías en el diálogo
        val autoCompleteCategoria = dialog.findViewById<AutoCompleteTextView>(R.id.auto_complete_categoria)

        // Lista de elementos para el AutoCompleteTextView
        val items = listOf(
            "Ficción",
            "No ficción",
            "Misterio",
            "Suspenso",
            "Ciencia ficción",
            "Fantasía",
            "Terror",
            "Romance",
            "Drama",
            "Biografía",
            "Historia",
            "Autoayuda",
            "Poesía",
            "Infantil",
            "Juvenil"
        )

        // Configurar el adaptador
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteCategoria.setAdapter(adapter)

        // Manejar la selección de elementos
        autoCompleteCategoria.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val itemSelected = items[position]
            showToast("Categoría seleccionada: $itemSelected")
        }

        btnAddLibro.setOnClickListener {
            val currentTitulo = titulo.text.toString()
            val currentAutor = autor.text.toString()
            val currentCategoria = autoCompleteCategoria.text.toString()
            val currentEditorial = editorial.text.toString()
            val currentPuntuacion = puntuacion.text.toString().toInt()
            val currentPagina = paginas.text.toString().toInt()
            val currentImagen = imagen.text.toString()


            if(currentTitulo.isNotEmpty() && currentAutor.isNotEmpty()
                && currentCategoria.isNotEmpty() && currentEditorial.isNotEmpty()){
                Libro(currentTitulo,currentAutor,currentCategoria,currentEditorial,currentPuntuacion,currentPagina,currentImagen,resena)
                // to do libro a bbdd
                dialog.hide()
            }

        }

        dialog.show()
    }


    private fun setUpAutoCompleteTextView() {
        autoCompleteTextView = findViewById(R.id.auto_complete)

        // Lista de elementos para el AutoCompleteTextView
        val items = listOf("Título", "Autor", "Editorial", "Categoría", "Puntuación")

        // Configurar el adaptador
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextView.setAdapter(adapter)

        // Manejar la selección de elementos
        autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val itemSelected = items[position]
            showToast("Item seleccionado: $itemSelected")
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }




    private fun navigateToDetail(id: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(EXTRA_ID, id)
        startActivity(intent)
    }

}