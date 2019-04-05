package com.balance.update.autobalanceupdate.data.db.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Filter(
        @PrimaryKey(autoGenerate = true) var key: Int? = null,
        var filterName: String,
        var filterPattern: List<String>
)

class FilterDiffCallback(private val oldListFilter: List<Filter>, private val newListFilter: List<Filter>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldListFilter[oldItemPosition].key == newListFilter[newItemPosition].key
    }

    override fun getOldListSize(): Int {
        return oldListFilter.size
    }

    override fun getNewListSize(): Int {
        return newListFilter.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldListFilter[oldItemPosition] == newListFilter[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}