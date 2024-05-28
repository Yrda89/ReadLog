package com.example.readlog

import android.annotation.SuppressLint
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
import androidx.appcompat.widget.SearchView

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.readlog.database.LibrosDatabase
import com.example.readlog.database.entities.LibrosEntity
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
        room = Room.databaseBuilder(this, LibrosDatabase::class.java, "libros").fallbackToDestructiveMigration().build()
        binding.searchView.setOnClickListener {}

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        initListeners()

        //llenarBaseDeDatos()
        setUpAutoCompleteTextView()
        initUI()


    }


    private fun initUI() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchByName(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?) = false
        })
        binding.rvLibros.setHasFixedSize(true)
        binding.rvLibros.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch {
            val listaLibros = room.getLibroDao().getLibrosOrdenTitulo()
            runOnUiThread() {

                adapter = LibroAdapter(listaLibros) { idLibro ->
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
        val paginasLeidas: EditText = dialog.findViewById(R.id.etPaginasLeidas)
        val imagen: EditText = dialog.findViewById(R.id.etImagen)

        // Configurar AutoCompleteTextView para categorías en el diálogo
        val autoCompleteCategoria =
            dialog.findViewById<AutoCompleteTextView>(R.id.auto_complete_categoria)

        // Lista de elementos para el AutoCompleteTextView
        val items = listOf(
            "Ficción",
            "No ficción",
            "Misterio",
            "Suspense",
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
            var currentPaginaInput = paginas.text.toString()
            var currentPaginaLeidaInput = paginas.text.toString()
            var currentImagen = imagen.text.toString()


            // Verificar si los campos requeridos están vacíos
            if (currentTitulo.isEmpty() || currentAutor.isEmpty()
                || currentCategoria.isEmpty() || currentEditorial.isEmpty()
            ) {
                // Mostrar un mensaje de aviso al usuario
                showToast("Por favor, complete todos los campos requeridos.")
                return@setOnClickListener // Salir de la función sin cerrar el diálogo
            }


            // Verificar si el valor ingresado en el campo de páginas es numérico
            val currentPagina = if (currentPaginaInput.matches("\\d+".toRegex())) {
                currentPaginaInput.toInt() // Convertir el valor a entero si es numérico
            } else {
                // Mostrar un mensaje de aviso al usuario si el valor no es numérico
                showToast("El valor de páginas debe ser un número.")
                // Establecer un valor predeterminado de 0
                0
            }

            // Verificar si el valor ingresado en el campo de páginas es numérico
            val currentPaginaLeida = if (currentPaginaLeidaInput.matches("\\d+".toRegex())) {
                currentPaginaInput.toInt() // Convertir el valor a entero si es numérico
            } else {
                // Mostrar un mensaje de aviso al usuario si el valor no es numérico
                showToast("El valor de páginas debe ser un número.")
                // Establecer un valor predeterminado de 0
                0
            }

            // Verificar si la imagen está en blanco o vacía
            if (currentImagen.isBlank()) {
                // Si la imagen está vacía, asignar una URL predeterminada
                currentImagen = "https://i.ibb.co/LrnRp03/Read-Log-Portada.png"
            } else if (!currentImagen.startsWith("https://")) {
                // Si la imagen no comienza con "https://", mostrar un mensaje de aviso
                showToast("La URL de la imagen debe comenzar con 'https://'.")
                return@setOnClickListener // Salir de la función sin cerrar el diálogo
            }


            CoroutineScope(Dispatchers.IO).launch {
                val existingLibro = room.getLibroDao().getLibroPorTitulo(currentTitulo)
                if (existingLibro != null) {
                    runOnUiThread {
                        showToast("El libro '${currentTitulo}' ya está en la lista.")
                    }
                } else {
                    val categoria =
                        room.getCategoriaDao().getCategoriaPorNombre(currentCategoria)
                    categoria?.let {
                        anadirLibro(
                            Libro(
                                currentTitulo,
                                currentAutor,
                                it.id_categoria,
                                currentEditorial,
                                currentPagina,
                                currentPaginaLeida,
                                currentImagen
                            )
                        )
                        runOnUiThread {
                            showToast("El libro se ha añadido correctamente")
                        }

                    } ?: run {

                    }
                    var listalib = room.getLibroDao().getLibrosOrdenTitulo()
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
                showToast("Ordenado por: $itemSelected")

                // Según el ítem seleccionado, llamar al método correspondiente para ordenar y actualizar la lista
                when (itemSelected) {
                    "Título" -> searchByTitle() // Método para ordenar por título
                    "Autor" -> searchByAuthor() // Método para ordenar por autor
                    "Editorial" -> searchByEditorial() // Método para ordenar por editorial
                    "Categoría" -> searchByCategory() // Método para ordenar por categoría
                }
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

    @SuppressLint("SuspiciousIndentation")
    private fun searchByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val listaLibros: List<LibrosEntity> = if (query.equals("*")) {
                room.getLibroDao().getLibrosOrdenTitulo()
            } else {
                room.getLibroDao().getLibrosPorTitulo("%$query%")
            }
            Log.i("Cuerpo de la consulta2", listaLibros.toString())
            runOnUiThread {
                adapter.updateList(listaLibros)
            }
        }
    }


    private fun llenarBaseDeDatos() {
        val libros = listOf(
            Libro(
                "It",
                "Stephen King",
                7,
                "Debols!llo",
                1168,
                915,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/It_%281986%29_front_cover%2C_first_edition.jpg/800px-It_%281986%29_front_cover%2C_first_edition.jpg"
            ),
            Libro(
                "Buscando a Alaska",
                "John Green",
                15,
                "Castillo",
                320,
                50,
                "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1347234921i/15999454.jpg"
            ),
            Libro(
                "Elantris",
                "Brandon Sanderson",
                6,
                "Nova",
                638,
                250,
                "https://m.media-amazon.com/images/I/81XE08B76cL._AC_UF894,1000_QL80_.jpg"
            ),
            Libro(
                "Cuentos de navidad",
                "Charles Dickens",
                1,
                "Alianza editorial",
                214,
                214,
                "https://marcialpons.es/media/img/portadas/9788413629902.jpg"
            ),
            Libro(
                "Fabricante de lágrimas",
                "Erin Doom",
                15,
                "Montena",
                640,
                345,
                "https://m.media-amazon.com/images/I/818dQkV5mrL._AC_UF1000,1000_QL80_.jpg"
            ),
            Libro(
                "Alas de sangre",
                "Rebecca Yarros",
                6,
                "Planeta",
                706,
                123,
                "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1689055697i/187622892.jpg"
            ),
            Libro(
                "Detrás del ruido",
                "Ángel Martín",
                10,
                "Planeta",
                256,
                0,
                "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1701611091i/198172802.jpg"
            ),
            Libro(
                "Encuentra tu persona vitamina",
                "Marian Rojas Estapé",
                12,
                "Planeta",
                318,
                231,
                "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1628382196i/58722360.jpg"
            ),
            Libro(
                "Hábitos atómicos",
                "James Clear",
                12,
                "Planeta",
                337,
                222,
                "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1555084828i/45028173.jpg"
            ),
            Libro(
                "1984",
                "George Orwell",
                1,
                "Debols!llo",
                368,
                284,
                "https://m.media-amazon.com/images/I/71sOSrd+JxL._AC_UF1000,1000_QL80_.jpg"
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            room.getCategoriaDao().llenarTablaCategorias()
            room.getLibroDao().borrarLibros()
            room.getLibroDao().borrarPrimaryKey()
            libros.forEach { libro ->
                Log.i("for each libro", libro.toString())

                room.getLibroDao().insertLibro(libro.toDatabase())
            }
            Log.i("select bbdd", room.getLibroDao().getAllLibros().toString())
        }
    }

    private fun searchByTitle() {
        CoroutineScope(Dispatchers.IO).launch {
            val listaLibros =
                room.getLibroDao().getLibrosOrdenTitulo() // Ordenar por defecto (título)
            runOnUiThread {
                Log.i("lista ordenada tit", listaLibros.toString())
                adapter.updateList(listaLibros)
            }
        }
    }

    private fun searchByAuthor() {
        CoroutineScope(Dispatchers.IO).launch {
            val listaLibros = room.getLibroDao().getAllLibrosPorAutor()
            runOnUiThread {
                adapter.updateList(listaLibros)
            }
        }
    }

    private fun searchByEditorial() {
        CoroutineScope(Dispatchers.IO).launch {
            val listaLibros = room.getLibroDao().getAllLibrosPorEditorial()
            runOnUiThread {
                adapter.updateList(listaLibros)
            }
        }
    }

    private fun searchByCategory() {
        CoroutineScope(Dispatchers.IO).launch {
            val listaLibros = room.getLibroDao().getAllLibrosPorCategoria()
            runOnUiThread {
                adapter.updateList(listaLibros)
            }
        }
    }


}