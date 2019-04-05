package com.balance.update.autobalanceupdate.data.db.dao

import androidx.room.*
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface UnresolvedSmsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(unresolvedSms: UnresolvedSms): Single<Long>

    @Query("SELECT * FROM UnresolvedSms")
    fun loadAll(): Observable<List<UnresolvedSms>>

    @Delete
    fun delete(filter: UnresolvedSms): Maybe<Int>

    @Update
    fun update(filter: UnresolvedSms): Maybe<Int>
}