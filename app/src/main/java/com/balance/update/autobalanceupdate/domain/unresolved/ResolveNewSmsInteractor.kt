package com.balance.update.autobalanceupdate.domain.unresolved

import com.balance.update.autobalanceupdate.data.db.entities.SmsPattern
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import com.balance.update.autobalanceupdate.domain.CompletableInteractor
import com.balance.update.autobalanceupdate.domain.spending.SaveSpendingSms
import com.balance.update.autobalanceupdate.domain.spending.SpendingInput
import io.reactivex.Completable
import io.reactivex.rxkotlin.toObservable

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

class TryResolveExistsUnresolvedSms : CompletableInteractor<List<UnresolvedSms>>() {
    override fun buildCase(params: List<UnresolvedSms>): Completable {
        return params.toObservable()
                .flatMapCompletable { sms ->
                    FindPatternForSms().attach(FindPatternForSmsInput(sms.sender, sms.body))
                            .onErrorReturnItem(SmsPattern(null, "", "", -1))
                            .flatMapCompletable {
                                SaveSpendingSms().attach(SpendingInput(
                                        sms.sender,
                                        sms.body,
                                        sms.dateInMillis,
                                        it
                                ))
                            }
                            .andThen(DeleteUnresolvedSms().attach(sms))
                            .onErrorComplete()
                            .onErrorComplete()

                }
    }

}
