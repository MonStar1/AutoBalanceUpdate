package com.balance.update.autobalanceupdate.presentation.filters.setup

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.SmsPattern
import com.balance.update.autobalanceupdate.domain.unresolved.DeleteSmsPattern
import com.balance.update.autobalanceupdate.domain.unresolved.SubscribeSmsPattern
import com.balance.update.autobalanceupdate.presentation.BasePresenter
import io.reactivex.rxkotlin.subscribeBy

class SetupFilterPresenter : BasePresenter<SetupFilterView>() {

    private val subscribeSmsPattern = SubscribeSmsPattern()
    private val deleteSmsPattern = DeleteSmsPattern()

    fun subscribePatterns(filter: Filter) {
        view?.showProgress(true)

        disposable.add(
                subscribeSmsPattern.execute(filter)
                        .doAfterNext { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onNext = { view?.setSmsPatterns(it) }
                        )
        )
    }

    fun deleteSmsPattern(smsPattern: SmsPattern) {
        view?.showProgress(true)

        disposable.add(
                deleteSmsPattern.execute(smsPattern)
                        .doAfterTerminate { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) }
                        )
        )
    }
}