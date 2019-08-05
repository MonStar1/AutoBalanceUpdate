package com.balance.update.autobalanceupdate.presentation.filters.datainfo

import android.app.Activity
import android.content.Intent
import android.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.domain.spending.SpendingWithPattern
import com.balance.update.autobalanceupdate.extension.loge
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.MvpView
import com.balance.update.autobalanceupdate.presentation.filters.setup.SetupFilterActivity
import kotlinx.android.synthetic.main.activity_setup_filter.*

interface SpendingView : MvpView {
    fun setSpendingList(list: List<SpendingWithPattern>)
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
    private val adapter = SpendingAdapter(listOf())

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

    override fun setSpendingList(list: List<SpendingWithPattern>) {
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

