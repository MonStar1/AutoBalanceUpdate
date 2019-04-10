package com.balance.update.autobalanceupdate.data.db.dao

import androidx.room.*
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface UnresolvedSmsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(unresolvedSms: UnresolvedSms): Single<Long>

    @Query("SELECT * FROM UnresolvedSms ORDER BY dateInMillis")
    fun loadAll(): Single<List<UnresolvedSms>>

    @Query("SELECT * FROM UnresolvedSms ORDER BY dateInMillis")
    fun subscribeAll(): Observable<List<UnresolvedSms>>

    @Query("SELECT * FROM UnresolvedSms WHERE `key` = :id LIMIT 1")
    fun loadById(id: Int): Single<UnresolvedSms>

    @Delete
    fun delete(unresolvedSms: UnresolvedSms): Completable

    @Update
    fun update(entity: UnresolvedSms): Maybe<Int>
}