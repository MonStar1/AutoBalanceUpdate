package com.balance.update.autobalanceupdate.presentation.filters.setup

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.SmsPattern
import com.balance.update.autobalanceupdate.data.db.entities.SmsPatternDiffCallback
import com.balance.update.autobalanceupdate.extension.loge
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.MvpView
import kotlinx.android.synthetic.main.activity_setup_filter.*

interface SetupFilterView : MvpView {
    fun setSmsPatterns(smsPatterns: List<SmsPattern>)

}

private const val EXTRA_FILTER = "EXTRA_FILTER"

class SetupFilterActivity : BasePresenterActivity<SetupFilterView>(), SetupFilterView {

    companion object {
        fun newInstance(activity: Activity, filter: Filter) {
            activity.startActivity(Intent(activity, SetupFilterActivity::class.java).apply {
                putExtra(EXTRA_FILTER, filter)
            })
        }
    }

    private lateinit var filter: Filter
    private val adapter = RVAdapter(listOf())

    override val presenter = SetupFilterPresenter()

    override val layoutId = R.layout.activity_setup_filter

    override fun doOnCreate() {
        filter = intent.getParcelableExtra(EXTRA_FILTER)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = "Filter: ${filter.filterName}"

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(getDrawable(R.drawable.divider_transparent)!!)
        })

        presenter.subscribePatterns(filter)
    }

    override fun onError(error: Throwable) {
        toast(this, error)
        loge(Exception(error))
    }

    override fun showProgress(isVisible: Boolean) {
        progress.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setSmsPatterns(smsPatterns: List<SmsPattern>) {
        adapter.setSmsPatterns(smsPatterns)
    }
}

private class RVAdapter(private var smsPatterns: List<SmsPattern>) : RecyclerView.Adapter<RVAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.view_setup_filter, parent, false))
    }

    override fun getItemCount(): Int {
        return smsPatterns.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val smsPattern = smsPatterns[position]

        holder.sender.text = smsPattern.sender
        holder.body.text = smsPattern.bodyPattern
    }

    fun setSmsPatterns(list: List<SmsPattern>) {
        val callback = SmsPatternDiffCallback(this.smsPatterns, list)
        val diffResult = DiffUtil.calculateDiff(callback, true)

        this.smsPatterns = list
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemByPosition(position: Int): SmsPattern {
        return smsPatterns[position]
    }

    private inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sender = itemView.findViewById<TextView>(R.id.sender)!!
        val body = itemView.findViewById<TextView>(R.id.body)
    }
}