package com.balance.update.autobalanceupdate.domain.filter

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.repository.FilterRepository
import com.balance.update.autobalanceupdate.domain.ObservableInteractor
import io.reactivex.Observable

class GetFilters : ObservableInteractor<List<Filter>, Unit>() {

    private val repository = FilterRepository()

    override fun buildCase(params: Unit): Observable<List<Filter>> {
        return repository.loadAll()
    }
}