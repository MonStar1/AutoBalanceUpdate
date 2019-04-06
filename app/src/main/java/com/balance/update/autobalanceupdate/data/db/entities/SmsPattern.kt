package com.balance.update.autobalanceupdate.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = Filter::class, parentColumns = ["key"], childColumns = ["filterId"], onDelete = ForeignKey.RESTRICT)])
data class SmsPattern(
        @PrimaryKey(autoGenerate = true) var key: Int? = null,
        val sender: String,
        val bodyPattern: String,
        val filterId: Int)