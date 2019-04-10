package com.balance.update.autobalanceupdate.data.repository

import com.balance.update.autobalanceupdate.App
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class FilterRepository {

    private val dao = App.db.getFilterDao()

    fun create(filterName: String): Single<Long> {
        return dao.insert(Filter(filterName = filterName))
    }

    fun subscribeAll(): Observable<List<Filter>> {
        return dao.subscribeAll()
    }

    fun subscribeAllByDateRange(startDate: Long, endDate: Long): Observable<List<Filter>> {
        return dao.subscribeAllByDateRange(startDate, endDate)
    }

    fun loadAll(): Single<List<Filter>> {
        return dao.loadAll()
    }

    fun loadFilterById(filterId: Int): Single<Filter> {
        return dao.loadFilterById(filterId)
    }

    fun delete(filter: Filter): Single<Int> {
        return dao.delete(filter)
    }

    fun update(filter: Filter): Single<Long> {
        return dao.update(filter)
    }

}