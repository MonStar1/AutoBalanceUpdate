package com.balance.update.autobalanceupdate.presentation.filters

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.memory.DateRange
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.filters.adapter.RVAdapter
import com.balance.update.autobalanceupdate.presentation.filters.datainfo.SpendingActivity
import com.balance.update.autobalanceupdate.presentation.unresolved.UnresolvedSmsActivity
import com.balance.update.autobalanceupdate.presentation.widget.CalendarDialogFragment
import com.balance.update.autobalanceupdate.presentation.widget.SwipeToDelete
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_filters.*
import java.text.SimpleDateFormat
import java.util.*

class FiltersActivity : BasePresenterActivity<FilterView>(), FilterView, CalendarDialogFragment.SelectDateCallback {

    override val presenter = FiltersPresenter()
    private val adapter = RVAdapter(listOf())
    private var calendarRange: CalendarRange? = null
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

    override fun setDateRange(dateRange: DateRange) {
        val startCalendar = Calendar.getInstance().apply {
            timeInMillis = dateRange.startDate
        }

        val endCalendar = Calendar.getInstance().apply {
            timeInMillis = dateRange.endDate
        }

        val startString = SimpleDateFormat.getDateInstance().format(startCalendar.time)
        val endString = SimpleDateFormat.getDateInstance().format(endCalendar.time)

        calendarRange = Pair(startCalendar, endCalendar)

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
                calendarRange?.let {
                    CalendarDialogFragment.newInstance(it.first, it.second).show(supportFragmentManager, null)
                }

//                val picker = SublimePicker(this).apply {
//                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                    initializePicker(SublimeOptions().apply {
//                        setDisplayOptions(ACTIVATE_DATE_PICKER)
//                        setCanPickDateRange(true)
//                    }, object : SublimeListenerAdapter() {
//                        override fun onDateTimeRecurrenceSet(sublimeMaterialPicker: SublimePicker?, selectedDate: SelectedDate?, hourOfDay: Int, minute: Int, recurrenceOption: SublimeRecurrencePicker.RecurrenceOption?, recurrenceRule: String?) {
//                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                        }
//
//                        override fun onCancelled() {
//                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                        }
//                    })
//
//                }
//
//                AlertDialog.Builder(this)
//                        .setView(picker)
//                        .setPositiveButton(android.R.string.ok) { _, _ ->
//                            //                            presenter.setSelectedDateRange(calendarView.startDate.timeInMillis, calendarView.endDate.timeInMillis)
//                        }
//                        .setNegativeButton(android.R.string.cancel, null)
//                        .setTitle("Select date range:")
//                        .show()
//                val calendarView =
//                        DateRangeCalendarView(this).apply {
//                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                            calendarRange?.let { this.setSelectedDateRange(it.first, it.second) }
//                        }
//
//                AlertDialog.Builder(this)
//                        .setView(calendarView)
//                        .setPositiveButton(android.R.string.ok) { _, _ ->
//                            presenter.setSelectedDateRange(calendarView.startDate.timeInMillis, calendarView.endDate.timeInMillis)
//                        }
//                        .setNegativeButton(android.R.string.cancel, null)
//                        .setTitle("Select date range:")
//                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRangeSelected(startDate: Calendar, endDate: Calendar) {
        presenter.setSelectedDateRange(startDate.timeInMillis, endDate.timeInMillis)
    }
}

typealias CalendarRange = Pair<Calendar, Calendar>

