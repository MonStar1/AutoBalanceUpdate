package com.balance.update.autobalanceupdate.domain.unresolved

import com.balance.update.autobalanceupdate.data.db.entities.SmsPattern
import com.balance.update.autobalanceupdate.data.repository.SmsPatternRepository
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Single

data class FindPatternForSmsInput(val sender: String, val body: String)

class FindPatternForSms : SingleInteractor<SmsPattern, FindPatternForSmsInput>() {
    private val smsPatternRepository = SmsPatternRepository()

    override fun buildCase(params: FindPatternForSmsInput): Single<SmsPattern> {
        return smsPatternRepository.loadAll()
                .map { list ->
                    list.find {
                        params.sender == it.sender && params.body.contains(it.bodyPattern)
                    }
                }
    }
}