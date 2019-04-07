package com.balance.update.autobalanceupdate.data.repository

import com.balance.update.autobalanceupdate.App
import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.SmsPattern
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class SmsPatternRepository {

    private val dao = App.db.getSmsPatternDao()

    fun create(sender: String, bodyPattern: String, filter: Filter): Single<Long> {
        return dao.insert(SmsPattern(sender = sender, bodyPattern = bodyPattern, filterId = filter.key!!))
    }

    fun subscribeAll(): Observable<List<SmsPattern>> {
        return dao.subscribeAll()
    }

    fun subscribeAllByFilter(filter: Filter): Observable<List<SmsPattern>> {
        return dao.subscribeAllByFilter(filter.key!!)
    }

    fun loadAll(): Single<List<SmsPattern>> {
        return dao.loadAll()
    }

    fun delete(entity: SmsPattern): Maybe<Int> {
        return dao.delete(entity)
    }

    fun update(entity: SmsPattern): Single<Long> {
        return dao.update(entity)
    }

}