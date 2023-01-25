package com.example.mytodo.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.liveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodo.R
import com.example.mytodo.core.models.Note
import com.example.mytodo.databinding.FragmentHomeBinding
import com.example.mytodo.viewmodels.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.notesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val editCallback: ((Note) -> Unit) = {
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(it.id)
            view.findNavController().navigate(action)
        }
        val notesAdapter = NotesAdapter(
            editCallback,
            {
                editCallback(it)
                true
            },
            {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.required_approvement))
                    .setMessage(getString(R.string.want_to_delete_note))
                    .setPositiveButton("Да") { _, _ ->
                        lifecycle.coroutineScope.launch {
                            homeViewModel.deleteNote(it)
                        }
                    }
                    .setNegativeButton("Нет") { _, _ -> }
                    .show()
                true
            }
        )
        recyclerView.adapter = notesAdapter
        homeViewModel.allNotes.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it)
        }

        registerForContextMenu(recyclerView)

        binding.addNewNoteBtn.setOnClickListener {
            // creates a new note
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(-1)
            view.findNavController().navigate(action)
        }
    }
}