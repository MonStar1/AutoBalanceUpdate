package com.balance.update.autobalanceupdate.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestData(
        @PrimaryKey(autoGenerate = true) var key: Int? = null,
        var value: String
)