package com.balance.update.autobalanceupdate.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LogEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val sender: String,
        val seller: String,
        val spent: Double,
        val actualBalance: Double,
        val categoryBalance : Double,
        val sellerText: String
)