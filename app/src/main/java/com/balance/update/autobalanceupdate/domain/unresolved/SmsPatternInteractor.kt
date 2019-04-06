package com.balance.update.autobalanceupdate.domain.unresolved

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import com.balance.update.autobalanceupdate.data.repository.SmsPatternRepository
import com.balance.update.autobalanceupdate.data.repository.UnresolvedSmsRepository
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Single

data class CreateSmsPatternInput(val filter: Filter, val sender: String, val bodyPattern: String, val unresolvedSms: UnresolvedSms)

class CreateSmsPattern : SingleInteractor<Long, CreateSmsPatternInput>() {

    private val repository = SmsPatternRepository()
    private val unresolvedRepository = UnresolvedSmsRepository()

    override fun buildCase(params: CreateSmsPatternInput): Single<Long> {
        return repository.create(params.sender, params.bodyPattern, params.filter)
                .flatMap { result ->
                    unresolvedRepository.delete(params.unresolvedSms).flatMapSingle { Single.just(result) }
                }
    }

}