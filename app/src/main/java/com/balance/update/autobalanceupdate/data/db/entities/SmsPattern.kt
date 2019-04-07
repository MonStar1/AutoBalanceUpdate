package com.balance.update.autobalanceupdate.data.db.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = Filter::class, parentColumns = ["key"], childColumns = ["filterId"], onDelete = ForeignKey.RESTRICT)])
data class SmsPattern(
        @PrimaryKey(autoGenerate = true) var key: Int? = null,
        val sender: String,
        val bodyPattern: String,
        val filterId: Int)

class SmsPatternDiffCallback(private val oldList: List<SmsPattern>, private val newList: List<SmsPattern>) : DiffUtil.Callback() {

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