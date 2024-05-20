package com.example.readlog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.example.readlog.databinding.ActivityDetailBinding
import com.example.readlog.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var room: LibrosDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        room = Room.databaseBuilder(this, LibrosDatabase::class.java, "libros").build()
        binding.ivBasura.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id: Int = intent.extras?.getInt(EXTRA_ID)?:0
        Log.i("id pasado",id.toString())
        getLibro(id)
    }
    private fun createUI(librosEntity: LibrosEntity){
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
            val message = ratingBar.rating.toString()
            Toast.makeText(this@DetailActivity, "La puntuacion es:"+ message, Toast.LENGTH_SHORT).show()
        }

        binding.tvTitulo.text = "Título: "+librosEntity.titulo
        binding.tvAutor.text = "Autor: "+librosEntity.autor
        binding.tvEditorial.text = "Editorial: " + librosEntity.editorial
        binding.tvPaginas.text = "Páginas: " + librosEntity.paginas
        //binding.rbEstrellas.numStars = 0
        Picasso.get().load(librosEntity.imagen).into(binding.ivImagen)

        binding.ivAtras.setOnClickListener { returnToMainActivity()}

    }
    @SuppressLint("SuspiciousIndentation")
    private fun getLibro(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val libro = room.getLibroDao().getLibroPorId(id)
            Log.i("id libro get libro",libro.toString())
                runOnUiThread { createUI(libro) }
        }
    }

    // Método para volver a la actividad principal (MainActivity)
    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
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
            // Actualizar la interfaz de usuario después de borrar el libro
            val listaLibros = room.getLibroDao().getAllLibros()
        /*    runOnUiThread {
                adapter.updateList(listaLibros)
            }     */
        }
    }


}