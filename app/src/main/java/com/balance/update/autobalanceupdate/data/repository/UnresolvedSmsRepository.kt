package com.balance.update.autobalanceupdate.data.repository

import com.balance.update.autobalanceupdate.App
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class UnresolvedSmsRepository {

    private val dao = App.db.getUnresolvedSmsDao()

    fun create(sender: String,
               body: String): Single<Long> {
        return dao.insert(UnresolvedSms(sender = sender, body = body))
    }

    fun subscribeAll(): Observable<List<UnresolvedSms>> {
        return dao.subscribeAll()
    }

    fun delete(unresolvedSms: UnresolvedSms): Maybe<Int> {
        return dao.delete(unresolvedSms)
    }

    fun update(unresolvedSms: UnresolvedSms): Maybe<Int> {
        return dao.update(unresolvedSms)
    }

}