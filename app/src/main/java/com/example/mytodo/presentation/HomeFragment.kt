package com.example.mytodo.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodo.R
import com.example.mytodo.databinding.FragmentHomeBinding
import com.example.mytodo.viewmodels.HomeViewModel
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
        val notesAdapter = NotesAdapter {
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(it.id)
            view.findNavController().navigate(action)
        }
        recyclerView.adapter = notesAdapter
        lifecycle.coroutineScope.launch {
            homeViewModel.getAllNotes().collect {
                notesAdapter.submitList(it)
            }
        }


        binding.addNewNoteBtn.setOnClickListener {
            // creates a new note
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(-1)
            view.findNavController().navigate(action)
        }
    }
}