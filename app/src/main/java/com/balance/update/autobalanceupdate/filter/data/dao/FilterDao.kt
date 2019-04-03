package com.balance.update.autobalanceupdate.filter.data.dao

import androidx.room.*
import com.balance.update.autobalanceupdate.filter.data.entities.Filter
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface FilterDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(filter: Filter): Single<Long>

    @Query("SELECT * FROM Filter")
    fun loadAll(): Observable<List<Filter>>

    @Delete
    fun delete(filter: Filter): Maybe<Int>

    @Update
    fun update(filter: Filter): Maybe<Int>
}