package com.balance.update.autobalanceupdate.data.repository

import com.balance.update.autobalanceupdate.data.memory.DateRange
import com.balance.update.autobalanceupdate.data.memory.DateRangeStore

class DateRangeRepository {

    private val dateRangeStore = DateRangeStore

    var dateRange: DateRange?
        get() = dateRangeStore.dateRange
        set(value) {
            dateRangeStore.dateRange = value
        }
}