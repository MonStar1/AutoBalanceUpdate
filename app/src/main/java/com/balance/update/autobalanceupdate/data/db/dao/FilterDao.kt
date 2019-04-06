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

    @Query("SELECT *, (SELECT COUNT(*) FROM SmsPattern as s WHERE s.filterId = f.`key`) as count FROM Filter as f")
    fun subscribeAll(): Observable<List<Filter>>

    @Query("SELECT *, (SELECT COUNT(*) FROM SmsPattern as s WHERE s.filterId = f.`key`) as count FROM Filter as f")
    fun loadAll(): Single<List<Filter>>

    @Query("SELECT * FROM Filter WHERE `key` == :filterId")
    fun loadFilterById(filterId: Int): Single<Filter>

    @Delete
    fun delete(filter: Filter): Maybe<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(filter: Filter): Single<Long>
}