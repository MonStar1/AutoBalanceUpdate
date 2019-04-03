package com.balance.update.autobalanceupdate.filter.data.entities

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun toListOfStrings(value: String): List<String> {
        return value.split("<<,>>")
    }

    @TypeConverter
    fun listToString(value: List<String>): String {
        return value.joinToString(separator = "<<,>>")
    }
}