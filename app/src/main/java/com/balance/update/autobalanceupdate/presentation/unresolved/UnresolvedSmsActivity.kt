package com.balance.update.autobalanceupdate.presentation.unresolved

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSmsDiffCallback
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.BasePresenterActivity
import com.balance.update.autobalanceupdate.presentation.entities.SelectedPattern
import com.balance.update.autobalanceupdate.presentation.entities.UnresolvedSmsCard
import com.balance.update.autobalanceupdate.presentation.widget.UnresolvedSmsNotification
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_unresolved_sms.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UnresolvedSmsActivity : BasePresenterActivity<UnresolvedSmsView>(), UnresolvedSmsView {

    override val presenter = UnresolvedSmsPresenter()
    override val layoutId = R.layout.activity_unresolved_sms

    private val adapter = RVAdapter(listOf())
    private val notification = UnresolvedSmsNotification(this)

    override fun doOnCreate() {
        setTitle(R.string.unresolved_sms)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        smsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(getDrawable(R.drawable.divider_transparent)!!)
        })
        smsRecyclerView.adapter = adapter

        adapter.onApplyListener = object : RVAdapter.OnApplyListener {
            override fun onApplyClicked(selectedPattern: SelectedPattern) {
                presenter.applyFilterTo(selectedPattern)
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

    override fun showNotification(countOfNotifications: Int) {
        notification.show(countOfNotifications)
    }

    override fun getUnresolvedSms(): List<UnresolvedSms> {
        return adapter.getList().map { it.unresolvedSms }
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
        holder.time.text = SimpleDateFormat.getDateTimeInstance().format(Date(sms.dateInMillis))
        holder.filterSpinner.adapter = FiltersSpinnerAdapter(holder.itemView.context, card.filters.toMutableList().apply {
            add(0, Filter(null, "None"))
        })
    }

    fun getList() = unresolvedSmsList

    fun setList(list: List<UnresolvedSmsCard>) {
        val oldList = this.unresolvedSmsList.map { it.unresolvedSms }
        val newList = list.map { it.unresolvedSms }

        val callback = UnresolvedSmsDiffCallback(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(callback, true)

        this.unresolvedSmsList = list
        diffResult.dispatchUpdatesTo(this)
    }

    private inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val applyButton = itemView.findViewById<Button>(R.id.applyButton)!!
        val sender = itemView.findViewById<TextView>(R.id.sender)!!
        val body = itemView.findViewById<TextView>(R.id.body)!!
        val filterSpinner = itemView.findViewById<Spinner>(R.id.filterSelection)!!
        val time = itemView.findViewById<TextView>(R.id.time)!!

        lateinit var unresolvedSmsCard: UnresolvedSmsCard
        lateinit var selectedBodyPattern: String

        init {
            applyButton.setOnClickListener(this)

            filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    checkApplyButtonState()
                }
            }

            body.customSelectionActionModeCallback = object : ActionMode.Callback {
                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = true
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu) = true

                override fun onDestroyActionMode(mode: ActionMode?) {
                    Completable.complete().delay(100, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                                checkApplyButtonState()
                            }.subscribe()
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu): Boolean {
                    while (menu.size() > 0) {
                        menu.removeItem(menu.getItem(0).itemId)
                    }

                    checkApplyButtonState()

                    return true
                }
            }
        }

        private fun checkApplyButtonState() {
            val selectedItem = (filterSpinner.selectedItem as Filter)

            val startPosition = body.selectionStart
            val endPosition = body.selectionEnd

            selectedBodyPattern = body.text.substring(startPosition until endPosition)

            applyButton.isEnabled = selectedItem.key != null && body.hasSelection()
        }

        override fun onClick(v: View?) {
            when (v) {
                applyButton -> {
                    onApplyListener?.onApplyClicked(SelectedPattern(
                            sender = unresolvedSmsCard.unresolvedSms.sender,
                            bodyPattern = selectedBodyPattern,
                            filter = (filterSpinner.selectedItem as Filter)
                    ))
                }
            }
        }
    }

    interface OnApplyListener {
        fun onApplyClicked(selectedPattern: SelectedPattern)
    }

}

private class FiltersSpinnerAdapter(context: Context, objects: List<Filter>) : ArrayAdapter<Filter>(context, android.R.layout.simple_list_item_1, objects)