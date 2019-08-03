package com.balance.update.autobalanceupdate.data.repository

import com.balance.update.autobalanceupdate.data.memory.DateRange
import com.balance.update.autobalanceupdate.data.memory.DateRangeMemoryStore

class DateRangeRepository {

    private val dateRangeStore = DateRangeMemoryStore

    var dateRange: DateRange?
        get() = dateRangeStore.dateRange
        set(value) {
            dateRangeStore.dateRange = value
        }
}