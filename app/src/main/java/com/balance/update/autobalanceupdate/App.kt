package com.balance.update.autobalanceupdate

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        )
                .addMigrations(object : Migration(2, 3) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE LogEntity ADD COLUMN timeInMillis INTEGER")
                    }

                })
                .fallbackToDestructiveMigration().build()
    }
}