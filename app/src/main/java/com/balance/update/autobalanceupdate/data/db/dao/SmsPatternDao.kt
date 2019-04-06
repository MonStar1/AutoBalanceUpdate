package com.balance.update.autobalanceupdate.data.db.dao

import androidx.room.*
import com.balance.update.autobalanceupdate.data.db.entities.SmsPattern
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface SmsPatternDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: SmsPattern): Single<Long>

    @Query("SELECT * FROM SmsPattern")
    fun subscribeAll(): Observable<List<SmsPattern>>

    @Query("SELECT * FROM SmsPattern")
    fun loadAll(): Single<List<SmsPattern>>

    @Query("SELECT * FROM SmsPattern WHERE filterId = :filterId")
    fun loadAllByFilter(filterId: Int): Observable<List<SmsPattern>>

    @Delete
    fun delete(entity: SmsPattern): Maybe<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: SmsPattern): Single<Long>
}