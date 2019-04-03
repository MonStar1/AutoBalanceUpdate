package com.balance.update.autobalanceupdate.filter.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.filter.data.entities.Filter
import com.balance.update.autobalanceupdate.filter.data.entities.FilterDiffCallback
import kotlinx.android.synthetic.main.activity_filters.*

class FiltersActivity : AppCompatActivity(), FilterView {

    private val presenter = FiltersPresenter()
    private val adapter = RVAdapter(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_filters)

        adapter.onFilterDelete = object : RVAdapter.OnFilterDelete {
            override fun onDelete(filter: Filter) {
                presenter.deleteFilter(filter)
            }
        }

        filtersRecyclerView.adapter = adapter
        filtersRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        createFilter.setOnClickListener {
            presenter.createNewFilter("test filter")
        }

        presenter.onViewAttached(this)
    }

    override fun setFilters(arrayOfFilters: List<Filter>) {
        adapter.setFilters(arrayOfFilters)
    }

    override fun onError(error: Throwable) {
        Log.e("Error_logoff", error.localizedMessage, error)
    }

    override fun showProgress(isVisible: Boolean) {
        progress.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
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