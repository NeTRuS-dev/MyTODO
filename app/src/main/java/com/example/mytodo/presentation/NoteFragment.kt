package com.example.mytodo.presentation

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodo.R
import com.example.mytodo.databinding.FragmentNoteBinding
import com.example.mytodo.viewmodels.NoteViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val ARG_ID = "noteId"

@AndroidEntryPoint
class NoteFragment : Fragment(), MenuProvider {
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
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

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
                        noteViewModel.deleteAlarm(it)
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        noteViewModel.note.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                menuInflater.inflate(R.menu.note_menu, menu)
                if (note.is_done) {
                    menu.findItem(R.id.mark_as_done).isVisible = false
                } else {
                    menu.findItem(R.id.mark_as_undone).isVisible = false
                }
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.add_alarm -> {
                if (noteViewModel.note.value == null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.save_note_first),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return true
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
                                selectedDateMilliseconds,
                                timePicker.hour,
                                timePicker.minute
                            )
                        }
                    }
                    timePicker.show(childFragmentManager, "alarmTimePicker")
                }
                datePicker.show(parentFragmentManager, "alarmDatePicker")
            }
            R.id.delete_note -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.required_approvement))
                    .setMessage(getString(R.string.want_to_delete_note))
                    .setPositiveButton("Да") { _, _ ->
                        lifecycle.coroutineScope.launch {
                            noteViewModel.deleteCurrentNote()
                        }.invokeOnCompletion {
                            if (it == null) {
                                NoteFragmentDirections.actionNoteFragmentToHomeFragment()
                                    .let { action ->
                                        view?.findNavController()?.navigate(action)
                                    }
                            } else {
                                Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT)
                                    .show()
                                Log.e("NoteFragment", "Ошибка", it)
                            }
                        }
                    }
                    .setNegativeButton("Нет") { _, _ -> }
                    .show()
                return true
            }
            R.id.mark_as_done -> {
                lifecycle.coroutineScope.launch {
                    noteViewModel.markNoteAsDone()
                }.invokeOnCompletion {
                    if (it == null) {
                        NoteFragmentDirections.actionNoteFragmentToHomeFragment()
                            .let { action ->
                                view?.findNavController()?.navigate(action)
                            }
                    } else {
                        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                        Log.e("NoteFragment", "Ошибка", it)
                    }
                }
                return true
            }
            R.id.mark_as_undone -> {
                lifecycle.coroutineScope.launch {
                    noteViewModel.markNoteAsNotDone()
                }.invokeOnCompletion {
                    if (it == null) {
                        NoteFragmentDirections.actionNoteFragmentToHomeFragment()
                            .let { action ->
                                view?.findNavController()?.navigate(action)
                            }
                    } else {
                        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                        Log.e("NoteFragment", "Ошибка", it)
                    }
                }
                return true
            }
        }
        return true
    }
}