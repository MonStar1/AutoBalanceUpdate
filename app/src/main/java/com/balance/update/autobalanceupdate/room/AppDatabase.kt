package com.balance.update.autobalanceupdate.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LogEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}