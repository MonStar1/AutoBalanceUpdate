package com.balance.update.autobalanceupdate.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface LogDao {
    @Query("SELECT * FROM LogEntity ORDER BY id DESC")
    fun getAll(): Observable<List<LogEntity>>

    @Insert
    fun insert(vararg entity: LogEntity) : Completable
}