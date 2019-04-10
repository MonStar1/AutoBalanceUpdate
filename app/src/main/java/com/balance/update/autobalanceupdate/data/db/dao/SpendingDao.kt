package com.balance.update.autobalanceupdate.data.db.dao

import androidx.room.*
import com.balance.update.autobalanceupdate.data.db.entities.Spending
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface SpendingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: Spending): Single<Long>

    @Query("SELECT * FROM Spending ORDER BY dateInMillis")
    fun subscribeAll(): Observable<List<Spending>>

    @Query("SELECT * FROM Spending ORDER BY dateInMillis")
    fun loadAll(): Single<List<Spending>>

    @Query("SELECT *, (SELECT sender FROM SmsPattern) as sender FROM Spending WHERE (SELECT filterId FROM SmsPattern WHERE smsPatternId = SmsPattern.`key`) = :filterId  ORDER BY dateInMillis")
    fun subscribeByFilterId(filterId: Int): Observable<List<Spending>>

    @Delete
    fun delete(entity: Spending): Single<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: Spending): Single<Long>
}