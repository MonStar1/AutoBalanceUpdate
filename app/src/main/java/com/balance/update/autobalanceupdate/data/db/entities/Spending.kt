package com.balance.update.autobalanceupdate.data.db.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = SmsPattern::class, parentColumns = ["key"], childColumns = ["smsPatternId"], onDelete = ForeignKey.RESTRICT)])
data class Spending(
        val spent: Double,
        val currency: String,
        val balance: Double,
        val dateInMillis: Long,
        val smsPatternId: Int,
        val sender: String = "",
        @PrimaryKey(autoGenerate = true)
        var key: Int? = null
)

class SpendingDiffCallback(private val oldList: List<Spending>, private val newList: List<Spending>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}