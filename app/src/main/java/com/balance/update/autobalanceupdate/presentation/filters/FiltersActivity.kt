package com.balance.update.autobalanceupdate.presentation.filters

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.FilterDiffCallback
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.unresolved.UnresolvedSmsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_filters.*

class FiltersActivity : BasePresenterActivity<FilterView>(), FilterView {

    override val presenter = FiltersPresenter()
    private val adapter = RVAdapter(listOf())
    override val layoutId = R.layout.activity_filters

    override fun doOnCreate() {
        setSupportActionBar(bottomAppBar)

        adapter.onFilterDelete = object : RVAdapter.OnFilterDelete {
            override fun onDelete(filter: Filter) {
                Snackbar.make(coordinator, "Delete filter \"${filter.filterName}\"?", Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.yes) { presenter.deleteFilter(filter) }.show()
            }
        }

        filtersRecyclerView.adapter = adapter
        filtersRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        createFilter.setOnClickListener {
            createNewFilter()
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
        }
        return super.onOptionsItemSelected(item)
    }
}


private class RVAdapter(private var filters: List<Filter>) : RecyclerView.Adapter<RVAdapter.VH>(), View.OnClickListener {

    var onFilterDelete: OnFilterDelete? = null

    override fun onClick(v: View?) {
        onFilterDelete?.onDelete(v?.tag as Filter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.view_filter, parent, false))
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val filter = filters[position]

        holder.delete.tag = filter
        holder.filterName.text = filter.filterName
    }

    fun setFilters(filters: List<Filter>) {
        val callback = FilterDiffCallback(this.filters, filters)
        val diffResult = DiffUtil.calculateDiff(callback, true)

        this.filters = filters
        diffResult.dispatchUpdatesTo(this)
    }

    private inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterName = itemView.findViewById<TextView>(R.id.name)!!
        val delete = itemView.findViewById<ImageView>(R.id.delete)!!

        init {
            delete.setOnClickListener(this@RVAdapter)
        }
    }

    interface OnFilterDelete {
        fun onDelete(filter: Filter)
    }
}