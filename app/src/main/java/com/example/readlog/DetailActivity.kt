package com.example.readlog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.readlog.MainActivity.Companion.EXTRA_ID
import com.example.readlog.database.LibrosDatabase
import com.example.readlog.database.entities.LibrosEntity
import com.example.readlog.database.entities.PuntuacionesEntity
import com.example.readlog.database.entities.toDataBase
import com.example.readlog.database.entities.toDatabase
import com.example.readlog.databinding.ActivityDetailBinding
import com.example.readlog.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var room: LibrosDatabase
    private lateinit var tvCampoResena: TextView
    private lateinit var rbEstrellas: RatingBar
    private var id: Int = 0
    private var contador_progreso: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        room = Room.databaseBuilder(this, LibrosDatabase::class.java, "libros").build()
        tvCampoResena = findViewById(R.id.tvCampoResena)
        binding.ivBasura.setOnClickListener {
            showDeleteConfirmationDialog()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        id = intent.extras?.getInt(EXTRA_ID)?:0
        Log.i("id pasado",id.toString())
        getLibro(id)
        calcularProgreso()
    }
    private fun createUI(librosEntity: LibrosEntity){
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val ratingBar = findViewById<RatingBar>(R.id.rbEstrellas)
        val button = findViewById<Button>(R.id.btnPuntuar)
        val ratingScale = findViewById<TextView>(R.id.tvMensajeEstrellas)
        ratingBar.setOnRatingBarChangeListener { rBar, fl, b ->
            ratingScale.text = fl.toString()
            when (rBar.rating.toInt()){
                1 -> ratingScale.text = "Muy malo"
                2 -> ratingScale.text = "Malo"
                3 -> ratingScale.text = "Regular"
                4 -> ratingScale.text = "Bueno"
                5 -> ratingScale.text = "Muy bueno"
                else -> ratingScale.text = " "
            }
        }

        button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val rating = ratingBar.rating.toInt()
                val existingPuntuacion = room.getPuntuacionesDao().getPuntuacionPorId(id)
                if (existingPuntuacion == null) {
                    anadirPuntuacion(Puntuacion(id, rating))
                } else {
                    actualizarPuntuacion(Puntuacion(id, rating))
                }
                withContext(Dispatchers.Main) {
                    val message = rating
                    Toast.makeText(
                        this@DetailActivity,
                        "La puntuacion es: " + message,
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }
        }
        binding.tvTitulo.text = "Título: "+librosEntity.titulo
        binding.tvAutor.text = "Autor: "+librosEntity.autor
        binding.tvEditorial.text = "Editorial: " + librosEntity.editorial
        binding.tvPaginas.text = "Páginas: " + librosEntity.paginas
        binding.tvPaginasLeidas.text = "Páginas leidas: " + librosEntity.paginasLeidas


        CoroutineScope(Dispatchers.IO).launch {
            val dao = room.getPuntuacionesDao()
            dao.insertPuntuacionWithDefaultValues()
            room.getResenasDao().insertResenaWithDefaultValues()

            val categoria = room.getCategoriaDao().getCategoriaPorId(librosEntity.id_categoria)
            val puntuacion = room.getPuntuacionesDao().getPuntuacionPorId(librosEntity.id_Libro)
            val puntuacionFloat = puntuacion?.toFloat() ?: 0f
            withContext(Dispatchers.Main) {
                binding.rbEstrellas.rating = puntuacionFloat
            }
            val resena =
                if(room.getResenasDao().getResenaPorId(librosEntity.id_Libro) != null){
                    room.getResenasDao().getResenaPorId(librosEntity.id_Libro)
            }else{
                ""
            }

            withContext(Dispatchers.Main) {
                binding.tvCampoResena.text = resena
                binding.tvCategoria.text = "Categoría: " + (categoria)
                binding.rbEstrellas.rating = puntuacion?.toFloat() ?: 0f
            }
        }

        Picasso.get()
            .load(librosEntity.imagen)
            .placeholder(R.drawable.read_log_portada)
            .error(R.drawable.read_log_portada)
            .into(binding.ivImagen)

        val progreso = if (librosEntity.paginas > 0) (librosEntity.paginasLeidas * 100) / librosEntity.paginas else 0
        progressBar.progress = progreso
        binding.tvTextProgreso.text = "$progreso%"

        binding.ivAtras.setOnClickListener { returnToMainActivity()}
        binding.ivEditResena.setOnClickListener { showWriteReviewDialog() }
        binding.ivEditLibro.setOnClickListener { showEditDialog() }
    }

    private fun showWriteReviewDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_resena, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        val editTextReview = dialogView.findViewById<EditText>(R.id.etResena)
        val buttonCancel = dialogView.findViewById<Button>(R.id.btnCancelar)
        val buttonSubmit = dialogView.findViewById<Button>(R.id.btnEnviar)

        CoroutineScope(Dispatchers.IO).launch {
            val existingResena = room.getResenasDao().getResenaPorId(id)
            withContext(Dispatchers.Main) {
                if (existingResena != null) {
                    editTextReview.setText(existingResena)
                }
            }
        }

        buttonCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        buttonSubmit.setOnClickListener {
            val reviewText = editTextReview.text.toString()
            if (reviewText.isBlank()) {
                Toast.makeText(this, "Por favor, escribe una reseña", Toast.LENGTH_SHORT).show()
            } else {
                val resena = Resena(id, reviewText)
                CoroutineScope(Dispatchers.IO).launch {
                    val existingResena = room.getResenasDao().getResenaPorId(id)
                    if (existingResena == null) {
                        anadirResena(resena)
                    } else {
                        actualizarResena(resena)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetailActivity, "Reseña enviada: $reviewText", Toast.LENGTH_SHORT).show()
                        binding.tvCampoResena.text = reviewText
                    }
                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.show()
    }

    private fun showEditDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_libro_edit)
        val btnActualizarLibro: Button = dialog.findViewById(R.id.btnActualizarLibroEdit)
        val titulo: EditText = dialog.findViewById(R.id.etTituloEdit)
        val autor: EditText = dialog.findViewById(R.id.etAutorEdit)
        val editorial: EditText = dialog.findViewById(R.id.etEditorialEdit)
        val paginas: EditText = dialog.findViewById(R.id.etPaginasEdit)
        val paginasLeidas: EditText = dialog.findViewById(R.id.etPaginasLeidasEdit)
        val imagen: EditText = dialog.findViewById(R.id.etImagenEdit)
        val autoCompleteCategoria = dialog.findViewById<AutoCompleteTextView>(R.id.auto_complete_categoria)

        val items = listOf(
            "Ficción", "No ficción", "Misterio", "Suspense", "Ciencia ficción", "Fantasía",
            "Terror", "Romance", "Drama", "Biografía", "Historia", "Autoayuda", "Poesía", "Infantil", "Juvenil"
        )
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteCategoria.setAdapter(adapter2)

        CoroutineScope(Dispatchers.IO).launch {
            val libroExistente = room.getLibroDao().getLibroPorId(id)
            withContext(Dispatchers.Main) {
                titulo.setText(libroExistente.titulo)
                autor.setText(libroExistente.autor)
                editorial.setText(libroExistente.editorial)
                paginas.setText(libroExistente.paginas.toString())
                paginasLeidas.setText(libroExistente.paginasLeidas.toString())
                imagen.setText(libroExistente.imagen)

                val categoria = room.getCategoriaDao().getCategoriaPorId(libroExistente.id_categoria)
                autoCompleteCategoria.setText(categoria, false)
            }
        }

        autoCompleteCategoria.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val itemSelected = items[position]
            showToast("Categoría seleccionada: $itemSelected")
        }

        btnActualizarLibro.setOnClickListener {
            val currentTitulo = titulo.text.toString()
            val currentAutor = autor.text.toString()
            val currentCategoria = autoCompleteCategoria.text.toString()
            val currentEditorial = editorial.text.toString()
            val currentPaginaInput = paginas.text.toString()
            val currentPaginaLeidaInput = paginasLeidas.text.toString()
            var currentImagen = imagen.text.toString()

            if (currentTitulo.isEmpty() || currentAutor.isEmpty() || currentCategoria.isEmpty() || currentEditorial.isEmpty()) {
                showToast("Por favor, complete todos los campos requeridos.")
                return@setOnClickListener
            }

            val currentPagina = currentPaginaInput.toIntOrNull() ?: run {
                showToast("El valor de páginas debe ser un número.")
                return@setOnClickListener
            }

            val currentPaginaLeida = currentPaginaLeidaInput.toIntOrNull() ?: run {
                showToast("El valor de páginas leídas debe ser un número.")
                return@setOnClickListener
            }

            if (currentImagen.isBlank()) {
                currentImagen = "https://i.ibb.co/LrnRp03/Read-Log-Portada.png"
            } else if (!currentImagen.startsWith("https://")) {
                showToast("La URL de la imagen debe comenzar con 'https://'.")
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val categoria = room.getCategoriaDao().getCategoriaPorNombre(currentCategoria)
                if (categoria != null) {
                    val libro = Libro(currentTitulo, currentAutor, categoria.id_categoria, currentEditorial, currentPagina, currentPaginaLeida, currentImagen)
                    val libroActualizado = actualizarLibro(libro)
                    if (libroActualizado != null) {
                        withContext(Dispatchers.Main) {
                            createUI(libroActualizado)
                            showToast("El libro se ha actualizado correctamente")
                            dialog.dismiss()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Categoría no válida")
                    }
                }
            }
        }

        dialog.show()
    }




    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun getLibro(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val libro = room.getLibroDao().getLibroPorId(id)
            Log.i("id libro get libro",libro.toString())
                runOnUiThread { createUI(libro) }
        }
    }


    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    @SuppressLint("SuspiciousIndentation")
    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Confirmación")
            .setMessage("¿Estás seguro de que quieres borrar este libro?")
            .setPositiveButton("Aceptar") { _, _ ->
                // Borrar el libro
                val idLibroToDelete = intent.getIntExtra(EXTRA_ID, 0)
                    deleteLibro(idLibroToDelete)
                    returnToMainActivity()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun deleteLibro(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            room.getLibroDao().borrarLibroPorId(id)
        }
    }

    private fun anadirPuntuacion(puntuacion: Puntuacion) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                room.getPuntuacionesDao().insertPuntuacion(puntuacion.toDataBase())
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "Error al añadir puntuación", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun anadirResena(resena: Resena) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                room.getResenasDao().insertResena(resena.toDataBase())
                withContext(Dispatchers.Main) {
                    tvCampoResena.text = resena.resena
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "Error al añadir reseña", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun actualizarResena(resena: Resena){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                room.getResenasDao().actualizarResena(resena.id_libro,resena.resena)
                withContext(Dispatchers.Main) {
                    tvCampoResena.text = resena.resena
                    Log.i("resena actualizada", room.getResenasDao().getResenaPorId(id))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "Error al actualizar reseña", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun actualizarPuntuacion(puntuacion: Puntuacion){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                room.getPuntuacionesDao().actualizarPuntuacion(puntuacion.id_libro,puntuacion.puntuacion)
                withContext(Dispatchers.Main) {
                    rbEstrellas.rating = puntuacion.puntuacion?.toFloat() ?: 0f
                }
            } catch (e: Exception) {
                /*
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "Error al actualizar puntuacion", Toast.LENGTH_SHORT).show()
                }*/
            }
        }
    }

    private suspend fun actualizarLibro(libro: Libro): LibrosEntity? {
        return try {
            room.getLibroDao().actualizarLibro(id, libro.titulo, libro.autor, libro.id_categoria, libro.editorial, libro.paginas, libro.paginasLeidas, libro.imagen)
            room.getLibroDao().getLibroPorId(id)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@DetailActivity, "Error al actualizar libro", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }


    private fun moverProgressBar(){
        binding.progressBar.progress = contador_progreso
        binding.tvTextProgreso.text = "$contador_progreso%"
        Log.i("contador progreso", contador_progreso.toString())
    }

    private fun calcularProgreso(){
        CoroutineScope(Dispatchers.IO).launch {
            val paginas = room.getLibroDao().getPaginasPorId(id)
            val paginasLeidas = room.getLibroDao().getPaginasLeidasPorId(id)
            contador_progreso = paginasLeidas * 100 / paginas
            Log.i("contador progreso 2", contador_progreso.toString())
            withContext(Dispatchers.Main) {
                moverProgressBar()
            }
        }
    }

}