package com.balance.update.autobalanceupdate.domain.unresolved

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.SmsPattern
import com.balance.update.autobalanceupdate.data.repository.SmsPatternRepository
import com.balance.update.autobalanceupdate.domain.ObservableInteractor
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Observable
import io.reactivex.Single

data class CreateSmsPatternInput(val filter: Filter, val sender: String, val bodyPattern: String)

class CreateSmsPattern : SingleInteractor<Long, CreateSmsPatternInput>() {

    private val repository = SmsPatternRepository()

    override fun buildCase(params: CreateSmsPatternInput): Single<Long> {
        return repository.create(params.sender, params.bodyPattern, params.filter)
    }

}

class SubscribeSmsPattern : ObservableInteractor<List<SmsPattern>, Filter>() {

    private val repository = SmsPatternRepository()

    override fun buildCase(params: Filter): Observable<List<SmsPattern>> {
        return repository.subscribeAllByFilter(params)
    }
}

class LoadSmsPatternById : SingleInteractor<SmsPattern, Int>() {

    private val repository = SmsPatternRepository()

    override fun buildCase(params: Int): Single<SmsPattern> {
        return repository.loadById(params)
    }
}

class DeleteSmsPattern : SingleInteractor<Int, SmsPattern>() {

    private val repository = SmsPatternRepository()

    override fun buildCase(params: SmsPattern): Single<Int> {
        return repository.delete(params)
    }

}