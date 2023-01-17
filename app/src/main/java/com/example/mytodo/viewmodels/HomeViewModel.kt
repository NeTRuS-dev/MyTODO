package com.example.mytodo.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mytodo.core.FileSystemWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(fileSystemWrapper: FileSystemWrapper) : ViewModel() {

}