package com.balance.update.autobalanceupdate.data.repository

import com.balance.update.autobalanceupdate.App
import com.balance.update.autobalanceupdate.data.db.entities.Spending
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class SpendingRepository {

    private val dao = App.db.getSpendingDao()

    fun create(spent: Double?,
               currency: String?,
               balance: Double?,
               dateInMillis: Long,
               smsPatternId: Int): Single<Long> {
        return dao.insert(Spending(spent, currency, balance, dateInMillis, smsPatternId))
    }

    fun subscribeAll(): Observable<List<Spending>> {
        return dao.subscribeAll()
    }

    fun loadAll(): Single<List<Spending>> {
        return dao.loadAll()
    }

    fun subscribeByFilterId(filterId: Int): Observable<List<Spending>> {
        return dao.subscribeByFilterId(filterId)
    }

    fun delete(entity: Spending): Single<Int> {
        return dao.delete(entity)
    }

    fun update(entity: Spending): Single<Long> {
        return dao.update(entity)
    }

}