package com.balance.update.autobalanceupdate

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.balance.update.autobalanceupdate.data.db.AppDatabase
import com.balance.update.autobalanceupdate.domain.filter.CreateIgnoreFilter

class App : Application() {

    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(this, AppDatabase::class.java, "app-db").fallbackToDestructiveMigration().build()

        CreateIgnoreFilter().execute(Unit).subscribe()
    }

}