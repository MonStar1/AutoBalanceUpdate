package com.balance.update.autobalanceupdate

import android.app.Application
import androidx.room.Room
import com.balance.update.autobalanceupdate.room.AppDatabase

class App : Application() {

    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "db-name"
        ).fallbackToDestructiveMigration().build()
    }
}