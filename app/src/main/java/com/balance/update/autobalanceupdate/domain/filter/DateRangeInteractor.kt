package com.balance.update.autobalanceupdate.domain.filter

import com.balance.update.autobalanceupdate.data.memory.DateRange
import com.balance.update.autobalanceupdate.data.repository.DateRangeRepository
import com.balance.update.autobalanceupdate.domain.CompletableInteractor
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

private val repository = DateRangeRepository()

class GetDateRange : SingleInteractor<DateRange, Unit>() {

    override fun buildCase(params: Unit): Single<DateRange> {
        if (repository.dateRange == null) {
            return Single.create {
                val startDate: Long
                val endDate: Long

                val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

                if (dayOfMonth > 7) {
                    startDate = Calendar.getInstance().run {
                        set(Calendar.DAY_OF_MONTH, 8)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 1)

                        timeInMillis
                    }

                    endDate = Calendar.getInstance().run {
                        add(Calendar.MONTH, 1)
                        set(Calendar.DAY_OF_MONTH, 7)
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)

                        timeInMillis
                    }
                } else {
                    startDate = Calendar.getInstance().run {
                        add(Calendar.MONTH, -1)
                        set(Calendar.DAY_OF_MONTH, 8)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 1)

                        timeInMillis
                    }

                    endDate = Calendar.getInstance().run {
                        set(Calendar.DAY_OF_MONTH, 7)
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)

                        timeInMillis
                    }
                }

                repository.dateRange = DateRange(startDate, endDate)

                it.onSuccess(repository.dateRange!!)
            }
        } else {
            return Single.just(repository.dateRange)
        }
    }
}

class SetDateRange : CompletableInteractor<DateRange>() {

    override fun buildCase(params: DateRange): Completable {
        return Completable.create {
            repository.dateRange = params

            it.onComplete()
        }
    }
}