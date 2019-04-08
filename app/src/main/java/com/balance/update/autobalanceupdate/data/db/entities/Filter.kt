package com.balance.update.autobalanceupdate.data.db.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Filter(
        @PrimaryKey(autoGenerate = true) var key: Int? = null,
        var filterName: String,
        var spent: Double = 0.0,
        val currency: String = "BYN"
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readDouble(),
            parcel.readString()) {
    }

    override fun toString(): String {
        return filterName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(key)
        parcel.writeString(filterName)
        parcel.writeDouble(spent)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Filter> {
        override fun createFromParcel(parcel: Parcel): Filter {
            return Filter(parcel)
        }

        override fun newArray(size: Int): Array<Filter?> {
            return arrayOfNulls(size)
        }
    }

}

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