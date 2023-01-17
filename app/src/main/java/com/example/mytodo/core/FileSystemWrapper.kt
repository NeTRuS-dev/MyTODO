package com.example.mytodo.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class FileSystemWrapper @Inject constructor(@ApplicationContext context: Context) {
    private val filesDir = context.filesDir

    fun listFilesDir(): Array<String> {
        return filesDir.list() ?: arrayOf()
    }

    fun getFile(fileName: String): File {
        return File(filesDir, fileName)
    }

    fun deleteFile(fileName: String): Boolean {
        return getFile(fileName).delete()
    }

    fun writeToFile(fileName: String, content: String) {
        val file = getFile(fileName)
        // create if not exists
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(content)
    }
}