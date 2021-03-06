package com.balance.update.autobalanceupdate

import android.app.Application
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.preferencesKey
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.balance.update.autobalanceupdate.room.AppDatabase

class App : Application() {

    companion object {

        lateinit var db: AppDatabase
    }

    val datastore = createDataStore("cards")
    val mtbankBalance = preferencesKey<Long>("mtbank")
    val priorBalance = preferencesKey<Long>("priorbank")

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
            .addMigrations(object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE LogEntity ADD COLUMN isSellerResolved INTEGER DEFAULT 0 NOT NULL")
                }
            })
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }
}