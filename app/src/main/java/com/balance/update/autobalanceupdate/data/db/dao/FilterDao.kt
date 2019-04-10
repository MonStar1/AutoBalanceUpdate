package com.balance.update.autobalanceupdate.data.db.dao

import androidx.room.*
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface FilterDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(filter: Filter): Single<Long>

    @Query("SELECT *, (SELECT SUM(spent) FROM Spending as sp WHERE (SELECT filterId FROM SmsPattern WHERE sp.smsPatternId = SmsPattern.`key`) = f.`key`) as spent FROM Filter as f")
    fun subscribeAll(): Observable<List<Filter>>

    @Query("SELECT *, (SELECT SUM(spent) FROM Spending as sp WHERE (SELECT filterId FROM SmsPattern WHERE sp.smsPatternId = SmsPattern.`key`) = f.`key` AND sp.dateInMillis >= :startDate AND sp.dateInMillis < :endDate) as spent FROM Filter as f")
    fun subscribeAllByDateRange(startDate: Long, endDate: Long): Observable<List<Filter>>

    @Query("SELECT *, (SELECT SUM(spent) FROM Spending as sp WHERE (SELECT filterId FROM SmsPattern WHERE sp.smsPatternId = SmsPattern.`key`) = f.`key`) as spent FROM Filter as f")
    fun loadAll(): Single<List<Filter>>

    @Query("SELECT * FROM Filter WHERE `key` == :filterId")
    fun loadFilterById(filterId: Int): Single<Filter>

    @Delete
    fun delete(filter: Filter): Single<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(filter: Filter): Single<Long>
}