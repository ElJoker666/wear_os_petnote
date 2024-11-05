package com.example.petnote_wear
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.TextView

class NotesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val noteTitle: TextView = findViewById(R.id.noteTitle)
        val noteContent: TextView = findViewById(R.id.noteContent)

        noteTitle.text = "Ejemplo de Título"
        noteContent.text = "Este es el contenido de la nota."
    }
}







