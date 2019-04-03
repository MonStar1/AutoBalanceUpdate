package com.balance.update.autobalanceupdate

import android.app.Application
import androidx.room.Room
import com.balance.update.autobalanceupdate.db.AppDatabase

class App : Application() {

    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(this, AppDatabase::class.java, "app-db").fallbackToDestructiveMigration().build()
    }

}