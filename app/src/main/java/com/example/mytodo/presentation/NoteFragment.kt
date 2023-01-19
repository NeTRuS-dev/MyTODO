package com.example.mytodo.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.mytodo.R
import com.example.mytodo.core.models.Note
import com.example.mytodo.databinding.FragmentNoteBinding
import com.example.mytodo.viewmodels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_ID = "noteId"

@AndroidEntryPoint
class NoteFragment : Fragment() {
    private val noteViewModel: NoteViewModel by viewModels()

    private lateinit var binding: FragmentNoteBinding

    private var note: Note? = null
    private var content: String? = null
    private var name: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val noteId = it.getInt(ARG_ID, -1)
            if (noteId != -1) {
                lifecycle.coroutineScope.launch {
                    note = noteViewModel.getNoteById(noteId)
                    note?.let { n ->
                        name = n.name
                        content = n.content
                    }
                }.invokeOnCompletion {
                    binding.noteTitle.setText(name)
                    binding.noteContent.setText(content)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noteSaveBtn.setOnClickListener {
            val noteName = binding.noteTitle.text.toString()
            val noteContent = binding.noteContent.text.toString()
            lifecycle.coroutineScope.launch {
                if (note == null) {
                    noteViewModel.addNewNote(noteName, noteContent)
                } else {
                    note!!.name = noteName
                    note!!.content = noteContent
                    noteViewModel.updateNote(note!!)
                }
            }.invokeOnCompletion {
                if (it == null) {
                    NoteFragmentDirections.actionNoteFragmentToHomeFragment().let { action ->
                        view.findNavController().navigate(action)
                    }
                }
            }
        }

    }
}