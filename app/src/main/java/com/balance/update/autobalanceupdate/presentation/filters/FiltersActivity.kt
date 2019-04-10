package com.balance.update.autobalanceupdate.presentation.filters

import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.FilterDiffCallback
import com.balance.update.autobalanceupdate.data.memory.DateRange
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.filters.datainfo.SpendingActivity
import com.balance.update.autobalanceupdate.presentation.unresolved.UnresolvedSmsActivity
import com.balance.update.autobalanceupdate.presentation.widget.SwipeToDelete
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_filters.*
import java.text.SimpleDateFormat
import java.util.*

class FiltersActivity : BasePresenterActivity<FilterView>(), FilterView {

    override val presenter = FiltersPresenter()
    private val adapter = RVAdapter(listOf())
    private var calendarPair: Pair<Calendar, Calendar>? = null
    override val layoutId = R.layout.activity_filters

    override fun doOnCreate() {
        setSupportActionBar(bottomAppBar)

        filtersRecyclerView.adapter = adapter
        filtersRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        ItemTouchHelper(SwipeToDelete(this, object : SwipeToDelete.OnItemDelete {
            override fun deleteItem(position: Int) {
                val filter = adapter.getItemByPosition(position)
                presenter.deleteFilter(filter)

                Snackbar.make(coordinator, "Undo filter \"${filter.filterName}\"?", Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.yes) { presenter.updateFilter(filter) }
                        .show()
            }

        })).attachToRecyclerView(filtersRecyclerView)

        createFilter.setOnClickListener {
            createNewFilter()
        }

        adapter.onFilterClickedListener = object : RVAdapter.OnFilterClickedListener {
            override fun onFilterClicked(filter: Filter) {
                SpendingActivity.newInstance(this@FiltersActivity, filter)
            }
        }
    }

    override fun setFilters(arrayOfFilters: List<Filter>) {
        adapter.setFilters(arrayOfFilters)
    }

    override fun onError(error: Throwable) {
        toast(this, error)
        Log.e("Exception_logoff", error.localizedMessage, error)
    }

    override fun showProgress(isVisible: Boolean) {
        progress.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setDateRange(dateRange: DateRange) {
        val startCalendar = Calendar.getInstance().apply {
            timeInMillis = dateRange.startDate
        }

        val endCalendar = Calendar.getInstance().apply {
            timeInMillis = dateRange.endDate
        }

        val startString = SimpleDateFormat.getDateInstance().format(startCalendar.time)
        val endString = SimpleDateFormat.getDateInstance().format(endCalendar.time)

        calendarPair = Pair(startCalendar, endCalendar)

        dateRangeTextView.text = getString(R.string.template_range_date, startString, endString)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    private fun createNewFilter() {
        val edit = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            showSoftInputOnFocus = true
            requestFocus()
        }

        AlertDialog.Builder(this)
                .setView(edit)
                .setPositiveButton(android.R.string.ok) { _, _ -> presenter.createNewFilter(edit.text.toString()) }
                .setNegativeButton(android.R.string.cancel, null)
                .setTitle("Type filter name:")
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filters, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_unresolved_sms -> {
                startActivity(Intent(this, UnresolvedSmsActivity::class.java))
                return true
            }
            R.id.action_select_date -> {
                val calendarView =
                        DateRangeCalendarView(this).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            calendarPair?.let { this.setSelectedDateRange(it.first, it.second) }
                        }

                AlertDialog.Builder(this)
                        .setView(calendarView)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            presenter.setSelectedDateRange(calendarView.startDate.timeInMillis, calendarView.endDate.timeInMillis)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .setTitle("Select date range:")
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}


private class RVAdapter(private var filters: List<Filter>) : RecyclerView.Adapter<RVAdapter.VH>() {

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

    private inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
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