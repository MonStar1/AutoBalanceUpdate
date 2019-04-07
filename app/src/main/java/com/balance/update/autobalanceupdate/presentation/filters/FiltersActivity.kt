package com.balance.update.autobalanceupdate.presentation.filters

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.FilterDiffCallback
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.filters.setup.SetupFilterActivity
import com.balance.update.autobalanceupdate.presentation.unresolved.UnresolvedSmsActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_filters.*

class FiltersActivity : BasePresenterActivity<FilterView>(), FilterView {

    override val presenter = FiltersPresenter()
    private val adapter = RVAdapter(listOf())
    override val layoutId = R.layout.activity_filters

    override fun doOnCreate() {
        setSupportActionBar(bottomAppBar)

        filtersRecyclerView.adapter = adapter
        filtersRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        ItemTouchHelper(SwipeToDelete(this)).attachToRecyclerView(filtersRecyclerView)

        createFilter.setOnClickListener {
            createNewFilter()
        }

        adapter.onFilterClickedListener = object : RVAdapter.OnFilterClickedListener {
            override fun onFilterClicked(filter: Filter) {
                SetupFilterActivity.newInstance(this@FiltersActivity, filter)
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

    private inner class SwipeToDelete(private val context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        private val background = ColorDrawable()
        private val bgColor = Color.RED
        private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
        private val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_forever_black_24dp)
        private val intrinsicWidth = deleteDrawable!!.intrinsicWidth
        private val intrinsicHeight = deleteDrawable!!.intrinsicHeight


        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val filter = adapter.getItemByPosition(position)
            presenter.deleteFilter(filter)

            Snackbar.make(coordinator, "Undo filter \"${filter.filterName}\"?", Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.yes) { presenter.updateFilter(filter) }
                    .show()
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
                c.drawRect(left, top, right, bottom, clearPaint);
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            val itemView = viewHolder.itemView
            val itemHeight = itemView.height

            val isCancelled = dX == 0.0F && !isCurrentlyActive

            if (isCancelled) {
                clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return
            }

            background.color = bgColor
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            background.draw(c)

            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight


            deleteDrawable!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteDrawable.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }


        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return 0.7F
        }

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
        holder.countLabel.text = filter.countOfSmsPatterns.toString()
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
        val countLabel = itemView.findViewById<TextView>(R.id.countLabel)!!
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