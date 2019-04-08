package com.balance.update.autobalanceupdate.presentation.filters.datainfo

import android.app.Activity
import android.content.Intent
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.Spending
import com.balance.update.autobalanceupdate.data.db.entities.SpendingDiffCallback
import com.balance.update.autobalanceupdate.extension.loge
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.MvpView
import com.balance.update.autobalanceupdate.presentation.filters.setup.SetupFilterActivity
import kotlinx.android.synthetic.main.activity_setup_filter.*
import java.text.SimpleDateFormat
import java.util.*

interface SpendingView : MvpView {
    fun setSpendingList(list: List<Spending>)
}

private const val EXTRA_FILTER = "EXTRA_FILTER"

class SpendingActivity : BasePresenterActivity<SpendingView>(), SpendingView {

    companion object {
        fun newInstance(activity: Activity, filter: Filter) {
            activity.startActivity(Intent(activity, SpendingActivity::class.java).apply {
                putExtra(EXTRA_FILTER, filter)
            })
        }
    }

    private lateinit var filter: Filter
    private val adapter = RVAdapter(listOf())

    override val presenter = SpendingPresenter()

    override val layoutId = R.layout.activity_setup_filter

    override fun doOnCreate() {
        filter = intent.getParcelableExtra(EXTRA_FILTER)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = "Spending for ${filter.filterName}"

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(getDrawable(R.drawable.divider_transparent)!!)
        })

        presenter.subscribeSpending(filter)
    }

    override fun onError(error: Throwable) {
        toast(this, error)
        loge(Exception(error))
    }

    override fun showProgress(isVisible: Boolean) {
        progress.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setSpendingList(list: List<Spending>) {
        adapter.setSpendingList(list)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_setup_filter -> {
                SetupFilterActivity.newInstance(this, filter)
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

private class RVAdapter(private var list: List<Spending>) : RecyclerView.Adapter<RVAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.view_spending, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val spending = list[position]

        holder.sender.text = spending.sender
        holder.spent.text = spending.spent.toString()
        holder.balance.text = spending.balance.toString()

        holder.time.text = SimpleDateFormat.getDateTimeInstance().format(Date(spending.dateInMillis))
        holder.currency.text = spending.currency
    }

    fun setSpendingList(list: List<Spending>) {
        val callback = SpendingDiffCallback(this.list, list)
        val diffResult = DiffUtil.calculateDiff(callback, true)

        this.list = list
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemByPosition(position: Int): Spending {
        return list[position]
    }

    private inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sender = itemView.findViewById<TextView>(R.id.sender)!!
        val spent = itemView.findViewById<TextView>(R.id.spent)!!
        val balance = itemView.findViewById<TextView>(R.id.balance)!!
        val time = itemView.findViewById<TextView>(R.id.time)!!
        val currency = itemView.findViewById<TextView>(R.id.currency)!!
    }
}