package com.balance.update.autobalanceupdate.presentation.filters.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.FilterDiffCallback
import com.balance.update.autobalanceupdate.extension.gone
import com.balance.update.autobalanceupdate.extension.invisible
import com.balance.update.autobalanceupdate.extension.visible

class FilterAdapter(private var filters: List<Filter>) : RecyclerView.Adapter<FilterAdapter.VH>() {

    var onFilterClickedListener: OnFilterClickedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.view_filter, parent, false))
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val filter = filters[position]

        holder.filter = filter
        holder.filterName.text = filter.filterName

        holder.spentLabel.text = holder.itemView.context.getString(R.string.template_amount_with_currency, filter.spent, filter.currency)

        if (filter.spent == 0.0) {
            holder.spentLabel.invisible()
        } else {
            holder.spentLabel.visible()
        }
    }

    fun setFilters(filters: List<Filter>) {
        val callback = FilterDiffCallback(this.filters, filters)
        val diffResult = DiffUtil.calculateDiff(callback, true)

        this.filters = filters
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemByPosition(position: Int): Filter {
        return filters[position]
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterName = itemView.findViewById<TextView>(R.id.name)!!
        val spentLabel = itemView.findViewById<TextView>(R.id.spentLabel)!!
        lateinit var filter: Filter

        init {
            itemView.setOnClickListener {
                onFilterClickedListener?.onFilterClicked(filter)
            }
        }
    }

    interface OnFilterClickedListener {
        fun onFilterClicked(filter: Filter)
    }
}