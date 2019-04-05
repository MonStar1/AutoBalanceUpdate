package com.balance.update.autobalanceupdate.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.balance.update.autobalanceupdate.data.db.dao.FilterDao
import com.balance.update.autobalanceupdate.data.db.dao.UnresolvedSmsDao
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms

@Database(entities = [Filter::class, UnresolvedSms::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFilterDao(): FilterDao

    abstract fun getUnresolvedSmsDao(): UnresolvedSmsDao
}

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