package com.balance.update.autobalanceupdate.domain.unresolved

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Single

class ResolveNewSms : SingleInteractor<Filter, SmsInput>() {

    override fun buildCase(params: SmsInput): Single<Filter> {
        return FindFilterForSms().execute(FindFilterForSmsInput(params.sender, params.body))
                .onErrorResumeNext { error ->
                    SetUnresolvedSms().execute(SmsInput(params.sender, params.body))
                            .map { throw error }
                }
    }
}