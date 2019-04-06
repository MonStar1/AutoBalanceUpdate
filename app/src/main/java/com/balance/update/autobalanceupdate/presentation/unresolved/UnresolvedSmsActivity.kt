package com.balance.update.autobalanceupdate.presentation.unresolved

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSmsCallback
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.entities.UnresolvedSmsCard
import kotlinx.android.synthetic.main.activity_unresolved_sms.*

class UnresolvedSmsActivity : BasePresenterActivity<UnresolvedSmsView>(), UnresolvedSmsView {

    override val presenter = UnresolvedSmsPresenter()
    override val layoutId = R.layout.activity_unresolved_sms

    private val adapter = RVAdapter(listOf())

    override fun doOnCreate() {
        setTitle(R.string.unresolved_sms)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        smsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(getDrawable(R.drawable.divider_transparent)!!)
        })
        smsRecyclerView.adapter = adapter

        adapter.onApplyListener = object : RVAdapter.OnApplyListener {
            override fun onApplyClicked(unresolvedSms: UnresolvedSms, filter: Filter) {
                presenter.applyFilterTo(filter, unresolvedSms)
            }
        }
    }

    override fun setUnresolvedSms(list: List<UnresolvedSmsCard>) {
        adapter.setList(list)
    }

    override fun onError(error: Throwable) {
        toast(this, error)
    }

    override fun showProgress(isVisible: Boolean) {
        progress.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}

private class RVAdapter(private var unresolvedSmsList: List<UnresolvedSmsCard>) : RecyclerView.Adapter<RVAdapter.VH>() {

    var onApplyListener: OnApplyListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.view_unresolved_sms, parent, false))
    }

    override fun getItemCount(): Int {
        return unresolvedSmsList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val card = unresolvedSmsList[position]
        val sms = card.unresolvedSms

        holder.unresolvedSmsCard = card
        holder.sender.text = sms.sender
        holder.body.text = sms.body
        holder.filterSpinner.adapter = FiltersSpinnerAdapter(holder.itemView.context, card.filters.toMutableList().apply {
            add(0, Filter(null, "None"))
        })
    }

    fun setList(list: List<UnresolvedSmsCard>) {
        val oldList = this.unresolvedSmsList.map { it.unresolvedSms }
        val newList = list.map { it.unresolvedSms }

        val callback = UnresolvedSmsCallback(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(callback, true)

        this.unresolvedSmsList = list
        diffResult.dispatchUpdatesTo(this)
    }

    private inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val applyButton = itemView.findViewById<Button>(R.id.applyButton)!!
        val sender = itemView.findViewById<TextView>(R.id.sender)!!
        val body = itemView.findViewById<TextView>(R.id.body)!!
        val filterSpinner = itemView.findViewById<Spinner>(R.id.filterSelection)!!

        lateinit var unresolvedSmsCard: UnresolvedSmsCard

        init {
            applyButton.setOnClickListener(this)
            filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = (filterSpinner.selectedItem as Filter)

                    applyButton.isEnabled = selectedItem.key != null
                }
            }
        }

        override fun onClick(v: View?) {
            when (v) {
                applyButton -> {
                    onApplyListener?.onApplyClicked(unresolvedSmsCard.unresolvedSms, filterSpinner.selectedItem as Filter)
                }
            }
        }
    }

    interface OnApplyListener {
        fun onApplyClicked(unresolvedSms: UnresolvedSms, filter: Filter)
    }

}

private class FiltersSpinnerAdapter(context: Context, objects: List<Filter>) : ArrayAdapter<Filter>(context, android.R.layout.simple_list_item_1, objects)