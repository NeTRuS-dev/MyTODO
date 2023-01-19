package com.example.mytodo.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class FileSystemWrapper @Inject constructor(@ApplicationContext context: Context) {
    private val filesDir = context.filesDir

    fun listFilesDir(): Array<Pair<String, String>> {
        val files = filesDir.listFiles() ?: return emptyArray()
        // get all file names from subdir
        return files.flatMap { file ->
            getFilesIn(file)
        }.toTypedArray()
    }

    private fun getFilesIn(path: File): List<Pair<String, String>> {
        if (path.isDirectory) {
            val subFiles = path.listFiles() ?: return emptyList()
            return subFiles.flatMap { subFile ->
                getFilesIn(subFile)
            }
        } else {
            return listOf((path.parentFile?.name ?: "") to path.name)
        }
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