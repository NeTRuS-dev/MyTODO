package com.example.mytodo.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodo.R
import com.example.mytodo.core.models.Note
import com.example.mytodo.databinding.FragmentNoteBinding
import com.example.mytodo.viewmodels.NoteViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

public const val ARG_ID = "noteId"

@AndroidEntryPoint
class NoteFragment : Fragment() {
    private val noteViewModel: NoteViewModel by viewModels()

    private lateinit var binding: FragmentNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val noteId = it.getInt(ARG_ID, -1)
            noteViewModel.noteId.postValue(noteId)
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

        noteViewModel.note.observe(viewLifecycleOwner) { note ->
            binding.noteTitle.setText(note?.name)
            binding.noteContent.setText(note?.content)
        }

        binding.noteSaveBtn.setOnClickListener {
            val noteName = binding.noteTitle.text.toString()
            val noteContent = binding.noteContent.text.toString()
            lifecycle.coroutineScope.launch {
                noteViewModel.writeNote(noteName, noteContent)
            }.invokeOnCompletion {
                if (it == null) {
                    NoteFragmentDirections.actionNoteFragmentToHomeFragment().let { action ->
                        view.findNavController().navigate(action)
                    }
                }
            }
        }

        val recyclerView = binding.noteAlerts
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val alarmsAdapter = AlarmsAdapter {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.required_approvement))
                .setMessage(getString(R.string.confirm_remove_alarm))
                .setPositiveButton("Да") { _, _ ->
                    lifecycle.coroutineScope.launch {
                        noteViewModel.deleteAlarm(requireContext(), it)
                    }
                }
                .setNegativeButton("Нет") { _, _ -> }
                .show()
            true
        }
        recyclerView.adapter = alarmsAdapter
        noteViewModel.noteAlarms.observe(viewLifecycleOwner) {
            alarmsAdapter.submitList(it)
        }

        binding.noteSetAlarmBtn.setOnClickListener {
            if (noteViewModel.note.value == null) {
                Toast.makeText(requireContext(), "Сначала сохраните заметку", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val datePicker = MaterialDatePicker
                .Builder
                .datePicker()
                .setTitleText(getString(R.string.chose_date))
                // set selection tomorrow
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds() + 24 * 60 * 60 * 1000)
                .build()
            datePicker.addOnPositiveButtonClickListener { selectedDateMilliseconds ->
                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText(getString(R.string.chose_time))
                    .build()
                timePicker.addOnPositiveButtonClickListener {
                    lifecycle.coroutineScope.launch {
                        noteViewModel.addAlarm(
                            requireContext(),
                            selectedDateMilliseconds,
                            timePicker.hour,
                            timePicker.minute
                        )
                    }
                }
                timePicker.show(childFragmentManager, "timePicker")
            }
            datePicker.show(parentFragmentManager, "datePicker")
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity)
            .supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()

        (requireActivity() as MainActivity)
            .supportActionBar
            ?.setDisplayHomeAsUpEnabled(false)
    }
}