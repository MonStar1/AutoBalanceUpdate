package com.balance.update.autobalanceupdate.presentation.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.appeaser.sublimepickerlibrary.SublimePicker
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions.ACTIVATE_DATE_PICKER
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.balance.update.autobalanceupdate.R
import java.util.*

class CalendarDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sublime_picker, null)

        val datePicker = view.findViewById<SublimePicker>(R.id.sublime_picker)

        datePicker.initializePicker(SublimeOptions().apply {
            setDisplayOptions(ACTIVATE_DATE_PICKER)
            setCanPickDateRange(true)
            setDateParams(getStartDate(this@CalendarDialogFragment), getEndDate(this@CalendarDialogFragment))
        }, object : SublimeListenerAdapter() {
            override fun onDateTimeRecurrenceSet(sublimeMaterialPicker: SublimePicker?, selectedDate: SelectedDate, hourOfDay: Int, minute: Int, recurrenceOption: SublimeRecurrencePicker.RecurrenceOption?, recurrenceRule: String?) {
                if (activity is SelectDateCallback) (activity as SelectDateCallback).onRangeSelected(selectedDate.startDate, selectedDate.endDate)

                dismiss()
            }

            override fun onCancelled() {
                dismiss()
            }
        })

        return view
    }

    companion object {
        private const val START_DATE_EXTRA = "START_DATE_EXTRA"
        private const val END_DATE_EXTRA = "END_DATE_EXTRA"

        fun newInstance(startDate: Calendar, endDate: Calendar): CalendarDialogFragment {
            return CalendarDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(START_DATE_EXTRA, startDate)
                    putSerializable(END_DATE_EXTRA, endDate)
                }
            }
        }

        private fun getStartDate(fragment: CalendarDialogFragment): Calendar {
            return fragment.arguments!!.getSerializable(START_DATE_EXTRA) as Calendar
        }

        private fun getEndDate(fragment: CalendarDialogFragment): Calendar {
            return fragment.arguments!!.getSerializable(END_DATE_EXTRA) as Calendar
        }
    }


    interface SelectDateCallback {
        fun onRangeSelected(startDate: Calendar, endDate: Calendar)
    }
}