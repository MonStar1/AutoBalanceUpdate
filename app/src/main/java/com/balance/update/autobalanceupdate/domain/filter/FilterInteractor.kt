package com.balance.update.autobalanceupdate.domain.filter

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.memory.DateRange
import com.balance.update.autobalanceupdate.data.repository.FilterRepository
import com.balance.update.autobalanceupdate.domain.CompletableInteractor
import com.balance.update.autobalanceupdate.domain.ObservableInteractor
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

private const val FILTER_IGNORE_KEY = -1

private val repository = FilterRepository()

class SubscribeFilters : ObservableInteractor<List<Filter>, Unit>() {

    override fun buildCase(params: Unit): Observable<List<Filter>> {
        return repository.subscribeAll()
    }
}

class SubscribeFiltersByRangeDate : ObservableInteractor<List<Filter>, DateRange>() {

    override fun buildCase(params: DateRange): Observable<List<Filter>> {
        return repository.subscribeAllByDateRange(params.startDate, params.endDate)
    }
}

class CreateFilter : SingleInteractor<Long, String>() {

    override fun buildCase(params: String): Single<Long> {
        return repository.create(params)
    }
}

class DeleteFilter : SingleInteractor<Int, Filter>() {

    override fun buildCase(params: Filter): Single<Int> {
        if (params.key == FILTER_IGNORE_KEY) {
            return Single.error(Throwable("Ignore filter can't be removed"))
        }
        return repository.delete(params)
    }

}

class UpdateFilter : SingleInteractor<Long, Filter>() {

    override fun buildCase(params: Filter): Single<Long> {
        if (params.key == FILTER_IGNORE_KEY) {
            return Single.error(Throwable("Ignore filter can't be updated"))
        }
        return repository.update(params)
    }

}

class CreateIgnoreFilter : CompletableInteractor<Unit>() {
    override fun buildCase(params: Unit): Completable =
            repository.loadFilterById(FILTER_IGNORE_KEY)
                    .ignoreElement()
                    .onErrorResumeNext { repository.create(filterId = FILTER_IGNORE_KEY, filterName = "Ignore").ignoreElement() }

}