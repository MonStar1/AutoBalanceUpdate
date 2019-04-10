package com.balance.update.autobalanceupdate.domain.filter

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.memory.DateRange
import com.balance.update.autobalanceupdate.data.repository.FilterRepository
import com.balance.update.autobalanceupdate.domain.ObservableInteractor
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Observable
import io.reactivex.Single

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
        return repository.delete(params)
    }

}

class UpdateFilter : SingleInteractor<Long, Filter>() {

    override fun buildCase(params: Filter): Single<Long> {
        return repository.update(params)
    }

}