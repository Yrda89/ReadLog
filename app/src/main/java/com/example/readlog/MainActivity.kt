package com.example.readlog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.readlog.database.LibrosDatabase
import com.example.readlog.database.entities.toDatabase
import com.example.readlog.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var binding: ActivityMainBinding
    private lateinit var addLibro: FloatingActionButton
    private lateinit var room: LibrosDatabase
    private lateinit var adapter: LibroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        room = Room.databaseBuilder(this, LibrosDatabase::class.java, "libros").build()
        binding.searchView.setOnClickListener {}

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        initListeners()

        llenarBaseDeDatos()
        setUpAutoCompleteTextView()
        initUI()




}


private fun initUI() {
    binding.rvLibros.setHasFixedSize(true)
    binding.rvLibros.layoutManager = LinearLayoutManager(this)
    Log.i("antes corrutina", "  ")
    CoroutineScope(Dispatchers.IO).launch {
        val listaLibros = room.getLibroDao().getAllLibros()
        val listaIdLibros = room.getLibroDao().getIdLibro()
        runOnUiThread() {
            Log.i("ui thread", "iniciando adapter")
            adapter = LibroAdapter(listaLibros) { position ->
                val idLibro = if (position < listaIdLibros.size) listaIdLibros[position] else 0
                navigateToDetail(idLibro)
            }
            binding.rvLibros.adapter = adapter

        }
    }
    Log.i("salida corrutina", "    ")
}


private fun initComponents() {
    addLibro = findViewById(R.id.addLibro)
}

private fun initListeners() {
    addLibro.setOnClickListener { showDialog() }
}

private fun showDialog() {
    val dialog = Dialog(this)
    dialog.setContentView(R.layout.dialog_libro)
    val btnAddLibro: Button = dialog.findViewById(R.id.btnAddLibro)

    val titulo: EditText = dialog.findViewById(R.id.etTitulo)
    val autor: EditText = dialog.findViewById(R.id.etAutor)
    val editorial: EditText = dialog.findViewById(R.id.etEditorial)
    val paginas: EditText = dialog.findViewById(R.id.etPaginas)
    val imagen: EditText = dialog.findViewById(R.id.etImagen)

    // Configurar AutoCompleteTextView para categorías en el diálogo
    val autoCompleteCategoria =
        dialog.findViewById<AutoCompleteTextView>(R.id.auto_complete_categoria)

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
    val adapter2 = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
    autoCompleteCategoria.setAdapter(adapter2)

    // Manejar la selección de elementos
    autoCompleteCategoria.onItemClickListener =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            val itemSelected = items[position]
            showToast("Categoría seleccionada: $itemSelected")
        }

    btnAddLibro.setOnClickListener {
        val currentTitulo = titulo.text.toString()
        val currentAutor = autor.text.toString()
        val currentCategoria =
            autoCompleteCategoria.text.toString() // Asumiendo que aquí obtienes el nombre de la categoría seleccionada
        val currentEditorial = editorial.text.toString()
        val currentPagina = paginas.text.toString().toInt()
        val currentImagen = imagen.text.toString()

        if (currentTitulo.isNotEmpty() && currentAutor.isNotEmpty()
            && currentCategoria.isNotEmpty() && currentEditorial.isNotEmpty()
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val categoria = room.getCategoriaDao().getCategoriaPorNombre(currentCategoria)
                categoria?.let {
                    anadirLibro(
                        Libro(
                            currentTitulo,
                            currentAutor,
                            it.id_categoria,
                            currentEditorial,
                            currentPagina,
                            currentImagen
                        )
                    )

                } ?: run {

                }
                Log.i(" ", room.getLibroDao().getAllLibros().toString())
                var listalib = room.getLibroDao().getAllLibros()
                runOnUiThread() {
                    adapter.updateList(listalib)

                }
            }
        }


        dialog.hide()

    }

    dialog.show()

}


private fun setUpAutoCompleteTextView() {
    autoCompleteTextView = findViewById(R.id.auto_complete)

    // Lista de elementos para el AutoCompleteTextView
    val items = listOf("Título", "Autor", "Editorial", "Categoría")

    // Configurar el adaptador
    val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
    autoCompleteTextView.setAdapter(adapter)

    // Manejar la selección de elementos
    autoCompleteTextView.onItemClickListener =
        AdapterView.OnItemClickListener { _, _, position, _ ->
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

private fun anadirLibro(libro: Libro) {
    CoroutineScope(Dispatchers.IO).launch {
        room.getLibroDao().insertLibro(libro.toDatabase())
    }
}


private fun llenarBaseDeDatos() {
    val libros = listOf(
        Libro("Título 1", "Autor 1", 1, "Editorial 1", 200, "imagen1.jpg"),
        Libro(
            "Título 2",
            "Autor 2",
            2,
            "Editorial 2",
            250,
            "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1347234921i/15999454.jpg"
        ),
        Libro(
            "Título 3",
            "Autor 3",
            1,
            "Editorial 1",
            180,
            "https://www.superherodb.com/pictures2/portraits/10/100/10441.jpg"
        ),
        Libro("Título 4", "Autor 4", 3, "Editorial 3", 300, "imagen4.jpg"),
        Libro("Título 5", "Autor 5", 2, "Editorial 2", 220, "imagen5.jpg"),
        Libro("Título 6", "Autor 6", 1, "Editorial 6", 200, "imagen6.jpg"),
        Libro("Título 7", "Autor 7", 2, "Editorial 7", 250, "imagen2.jpg"),
        Libro("Título 8", "Autor 8", 1, "Editorial 8", 180, "imagen3.jpg"),
        Libro("Título 9", "Autor 9", 3, "Editorial 9", 300, "imagen4.jpg"),
        Libro("Título 10", "Autor 10", 2, "Editorial 10", 220, "imagen5.jpg")
    )

    CoroutineScope(Dispatchers.IO).launch {
        room.getCategoriaDao().llenarTablaCategorias()
        room.getLibroDao().borrarLibros()
        room.getLibroDao().borrarPrimaryKey()
        libros.forEach { libro ->
            Log.i("for each libro", libro.toString())

            room.getLibroDao().insertLibro(libro.toDatabase())
        }
        Log.i("select bbdd",room.getLibroDao().getAllLibros().toString())
    }
}
}