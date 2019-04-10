package com.balance.update.autobalanceupdate.presentation.filters.datainfo

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.domain.spending.SubscribeSpendingWithPatternByFilter
import com.balance.update.autobalanceupdate.presentation.BasePresenter
import io.reactivex.rxkotlin.subscribeBy

class SpendingPresenter : BasePresenter<SpendingView>() {

    private val loadSpending = SubscribeSpendingWithPatternByFilter()

    fun subscribeSpending(filter: Filter) {
        view?.showProgress(true)

        disposable.add(
                loadSpending.execute(filter)
                        .doOnNext { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onNext = { view?.setSpendingList(it) }
                        )
        )
    }
}