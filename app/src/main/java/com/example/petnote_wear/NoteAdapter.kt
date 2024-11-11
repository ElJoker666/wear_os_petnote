package com.example.petnote_wear

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petnote_wear.databinding.ItemNoteBinding

class NoteAdapter(private val notes: List<Note>) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.descriptionTextView.text = note.description
        holder.endDateTextView.text = note.endDate
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.noteTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.noteDescription)
        val endDateTextView: TextView = view.findViewById(R.id.noteEndDate)
    }
}

