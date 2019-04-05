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
        smsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        smsRecyclerView.adapter = adapter
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

private class RVAdapter(private var unresolvedSmsList: List<UnresolvedSmsCard>) : RecyclerView.Adapter<RVAdapter.VH>(), View.OnClickListener {

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.view_unresolved_sms, parent, false))
    }

    override fun getItemCount(): Int {
        return unresolvedSmsList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val sms = unresolvedSmsList[position].unresolvedSms

        holder.sender.text = sms.sender
        holder.body.text = sms.body
        holder.filterSpinner.adapter = FiltersSpinnerAdapter(holder.itemView.context, unresolvedSmsList[position].filters)
    }

    fun setList(list: List<UnresolvedSmsCard>) {
        val oldList = this.unresolvedSmsList.map { it.unresolvedSms }
        val newList = list.map { it.unresolvedSms }

        val callback = UnresolvedSmsCallback(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(callback, true)

        this.unresolvedSmsList = list
        diffResult.dispatchUpdatesTo(this)
    }

    private inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val applyButton = itemView.findViewById<Button>(R.id.applyButton)!!
        val sender = itemView.findViewById<TextView>(R.id.sender)!!
        val body = itemView.findViewById<TextView>(R.id.body)!!
        val filterSpinner = itemView.findViewById<Spinner>(R.id.body)!!

        init {
            applyButton.setOnClickListener(this@RVAdapter)
        }
    }

}

private class FiltersSpinnerAdapter(context: Context, objects: List<Filter>) : ArrayAdapter<Filter>(context, android.R.layout.simple_list_item_1, objects)