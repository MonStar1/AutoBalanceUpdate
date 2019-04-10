package com.balance.update.autobalanceupdate.data.memory

data class DateRange(val startDate: Long, val endDate: Long)

object DateRangeStore {
    var dateRange: DateRange? = null
}