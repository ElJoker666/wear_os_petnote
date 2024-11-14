package com.example.petnote_wear

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale

// Define el formato de fecha, reemplazando "dd/MM/yyyy" con el formato que estás usando para endDate
val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private val notesList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        // Inicialización de Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Configuración del RecyclerView
        notesRecyclerView = findViewById(R.id.notes_recycler_view)
        notesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener el usuario autenticado
        val user = auth.currentUser
        if (user != null) {
            // Si el usuario está autenticado, cargar sus notas
            loadUserNotes(user.email ?: "")
        } else {
            // Si no está autenticado, intentar autenticarlo o mostrar un error
            authenticateUser()
        }

        // Configurar el botón de refrescar
        val refreshButton: Button = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                loadUserNotes(user.email ?: "")  // Recargar las notas del usuario
            } else {
                authenticateUser()  // Intentar autenticar si no hay usuario
            }
        }
    }

    private fun authenticateUser() {
        val email = "jutanes2002@gmail.com"
        val password = "nacien2002"

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    loadUserNotes(user?.email ?: "")
                } else {
                    Log.d("Authentication", "Authentication failed: ${task.exception?.message}")
                    Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadUserNotes(userEmail: String) {
        if (userEmail.isNotEmpty()) {
            firestore.collection("users")
                .document(userEmail)
                .collection("notes")
                .get()
                .addOnSuccessListener { documents ->
                    notesList.clear()  // Limpiar la lista antes de agregar nuevas notas
                    for (document in documents) {
                        val title = document.getString("title") ?: "Sin título"
                        val description = document.getString("description") ?: "Sin descripción"
                        val endDate = document.getString("endDate") ?: "Sin fecha de fin"

                        // Filtrar las notas cuya fecha aún no ha pasado
                        val noteDate = dateFormat.parse(endDate)
                        val currentDate = System.currentTimeMillis()
                        if (noteDate != null && noteDate.time >= currentDate) {
                            notesList.add(Note(title, description, endDate))
                        }
                    }

                    // Ordenar las notas por fecha en orden ascendente (de la más antigua a la más reciente)
                    notesList.sortBy { note ->
                        dateFormat.parse(note.endDate)?.time ?: 0L  // Ordena por la fecha en formato de milisegundos
                    }

                    // Configurar el adaptador con la lista ordenada
                    noteAdapter = NoteAdapter(notesList)
                    notesRecyclerView.adapter = noteAdapter
                }
                .addOnFailureListener { e ->
                    Log.d("Firestore Error", "Error al cargar notas: ${e.localizedMessage}")
                    Toast.makeText(this, "Error al cargar notas: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}