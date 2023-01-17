package com.example.mytodo.core.models

import java.util.Date

data class Note(val name: String, val content: String, val dateTime: Date, val directory: String) {
    fun getDateTimeString(): String {
        return dateTime.toString()
    }
}
