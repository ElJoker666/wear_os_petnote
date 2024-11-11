package com.example.petnote_wear

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    }

    private fun authenticateUser() {
        val email = "jutanes2002@gmail.com"
        val password = "nacien2002"

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d("User", "User authenticated: ${user?.email}")
                    loadUserNotes(user?.email ?: "")
                } else {
                    Log.d("Authentication", "Authentication failed: ${task.exception?.message}")
                    Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Modificado para usar los nombres correctos de los campos: title, description y endDate
    private fun loadUserNotes(userEmail: String) {
        if (userEmail.isNotEmpty()) {
            // Cargar las notas del usuario utilizando su correo como userId
            firestore.collection("users")
                .document(userEmail)  // Usamos el email del usuario autenticado
                .collection("notes")
                .get()
                .addOnSuccessListener { documents ->
                    notesList.clear()  // Limpiar la lista antes de agregar nuevas notas
                    for (document in documents) {
                        val title = document.getString("title") ?: "Sin título"
                        val description = document.getString("description") ?: "Sin descripción"
                        val endDate = document.getString("endDate") ?: "Sin fecha de fin"
                        notesList.add(Note(title, description, endDate))
                    }
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
