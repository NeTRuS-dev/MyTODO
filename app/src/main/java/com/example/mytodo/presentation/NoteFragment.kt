package com.example.mytodo.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mytodo.R
import com.example.mytodo.core.models.Note
import com.example.mytodo.databinding.FragmentNoteBinding
import com.example.mytodo.viewmodels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_ID = "noteId"

@AndroidEntryPoint
class NoteFragment : Fragment() {
    private val noteViewModel: NoteViewModel by viewModels()

    private lateinit var binding: FragmentNoteBinding

    private var note: Note? = null
    private var directory: String? = null
    private var name: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val noteId = it.getInt(ARG_ID, -1)
            if (noteId != -1) {
                note = noteViewModel.getNoteById(noteId)
                note?.let { n ->
                    directory = n.name
                    name = n.content
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }
}