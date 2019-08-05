package com.balance.update.autobalanceupdate.presentation.filters.datainfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.SpendingDiffCallback
import com.balance.update.autobalanceupdate.domain.spending.SpendingWithPattern
import com.balance.update.autobalanceupdate.extension.gone
import com.balance.update.autobalanceupdate.extension.ifNull
import com.balance.update.autobalanceupdate.extension.visible
import java.text.SimpleDateFormat
import java.util.*

class SpendingAdapter(private var list: List<SpendingWithPattern>) : RecyclerView.Adapter<SpendingAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.view_spending, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val spending = list[position].spending
        val smsPattern = list[position].smsPattern

        val context = holder.itemView.context

        holder.place.text = smsPattern.bodyPattern
        holder.sender.text = spending.sender
        holder.spent.text = context.getString(R.string.template_amount_with_currency, spending.spent, spending.currency)
        holder.balance.text = context.getString(R.string.template_amount_with_currency, spending.balance, spending.currency)

        holder.time.text = SimpleDateFormat.getDateTimeInstance().format(Date(spending.dateInMillis))

        spending.balance.ifNull(
                ifNotNull = {
                    holder.balanceLabel.visible()
                    holder.balance.visible()
                },
                ifNull = {
                    holder.balanceLabel.gone()
                    holder.balance.gone()
                }
        )
        spending.spent.ifNull(
                ifNotNull = { holder.spent.visible() },
                ifNull = { holder.spent.gone() }
        )
    }

    fun setSpendingList(list: List<SpendingWithPattern>) {
        val oldList = this.list.map { it.spending }
        val newList = list.map { it.spending }

        val callback = SpendingDiffCallback(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(callback, true)

        this.list = list
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemByPosition(position: Int): SpendingWithPattern {
        return list[position]
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sender = itemView.findViewById<TextView>(R.id.sender)!!
        val spent = itemView.findViewById<TextView>(R.id.spent)!!
        val balance = itemView.findViewById<TextView>(R.id.balance)!!
        val time = itemView.findViewById<TextView>(R.id.time)!!
        val place = itemView.findViewById<TextView>(R.id.place)!!
        val balanceLabel = itemView.findViewById<TextView>(R.id.balanceLabel)!!
    }
}