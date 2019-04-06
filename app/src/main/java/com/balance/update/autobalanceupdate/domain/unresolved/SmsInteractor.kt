package com.balance.update.autobalanceupdate.domain.unresolved

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import com.balance.update.autobalanceupdate.data.repository.UnresolvedSmsRepository
import com.balance.update.autobalanceupdate.domain.ObservableInteractor
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Observable
import io.reactivex.Single

private val repository = UnresolvedSmsRepository()

class GetUnresolvedSms : ObservableInteractor<List<UnresolvedSms>, Unit>() {

    override fun buildCase(params: Unit): Observable<List<UnresolvedSms>> {
        return repository.loadAll()
    }
}

data class SmsInput(val sender: String, val body: String)

class SetUnresolvedSms : SingleInteractor<Long, SmsInput>() {

    override fun buildCase(params: SmsInput): Single<Long> {
        return repository.create(params.sender, params.body)
    }

}
