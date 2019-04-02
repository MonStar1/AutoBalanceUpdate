package com.balance.update.autobalanceupdate.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TestDao {

    @Insert
    fun insert(testData: TestData)

    @Query("SELECT * FROM TestData")
    fun loadAll(): Array<TestData>
}