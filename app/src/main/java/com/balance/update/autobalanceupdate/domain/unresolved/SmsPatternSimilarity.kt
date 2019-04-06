package com.balance.update.autobalanceupdate.domain.unresolved

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.repository.FilterRepository
import com.balance.update.autobalanceupdate.data.repository.SmsPatternRepository
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Single

data class FindFilterForSmsInput(val sender: String, val body: String)

class FindFilterForSms : SingleInteractor<Filter, FindFilterForSmsInput>() {
    private val smsPatternRepository = SmsPatternRepository()
    private val filterRepository = FilterRepository()

    override fun buildCase(params: FindFilterForSmsInput): Single<Filter> {
        return smsPatternRepository.loadAll()
                .map { list ->
                    list.find {
                        params.sender == it.sender && params.body.contains(it.bodyPattern)
                    }
                }.flatMap {
                    filterRepository.loadFilterById(it.filterId)
                }
    }
}