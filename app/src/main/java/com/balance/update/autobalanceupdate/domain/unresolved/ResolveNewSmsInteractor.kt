package com.balance.update.autobalanceupdate.domain.unresolved

import com.balance.update.autobalanceupdate.domain.CompletableInteractor
import com.balance.update.autobalanceupdate.domain.spending.SaveSpendingSms
import com.balance.update.autobalanceupdate.domain.spending.SpendingInput
import io.reactivex.Completable

data class ResolveSmsInput(val sender: String, val body: String, val dateInMillis: Long)

class ResolveNewSms : CompletableInteractor<ResolveSmsInput>() {

    override fun buildCase(params: ResolveSmsInput): Completable {
        return FindPatternForSms().attach(FindPatternForSmsInput(params.sender, params.body))
                .flatMapCompletable {
                    SaveSpendingSms().attach(SpendingInput(
                            params.sender,
                            params.body,
                            params.dateInMillis,
                            it
                    ))
                }
                .onErrorResumeNext { error ->
                    SetUnresolvedSms().attach(SmsInput(params.sender, params.body, params.dateInMillis))
                            .map { throw error }.ignoreElement()
                }
    }
}