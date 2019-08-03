package com.balance.update.autobalanceupdate.data.memory

data class DateRange(val startDate: Long, val endDate: Long)

object DateRangeMemoryStore {
    var dateRange: DateRange? = null
}