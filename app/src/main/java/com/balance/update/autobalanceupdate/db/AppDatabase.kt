package com.balance.update.autobalanceupdate.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.balance.update.autobalanceupdate.filter.data.dao.FilterDao
import com.balance.update.autobalanceupdate.filter.data.entities.Converters
import com.balance.update.autobalanceupdate.filter.data.entities.Filter

@Database(entities = [Filter::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFilterDao(): FilterDao
}