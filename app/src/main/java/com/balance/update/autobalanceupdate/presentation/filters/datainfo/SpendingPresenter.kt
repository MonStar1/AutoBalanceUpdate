package com.balance.update.autobalanceupdate.presentation.filters.datainfo

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.domain.spending.SubscribeSpendingByFilter
import com.balance.update.autobalanceupdate.presentation.BasePresenter
import io.reactivex.rxkotlin.subscribeBy

class SpendingPresenter : BasePresenter<SpendingView>() {

    private val loadSpendingByFilter = SubscribeSpendingByFilter()

    fun subscribeSpending(filter: Filter) {
        view?.showProgress(true)

        disposable.add(
                loadSpendingByFilter.execute(filter)
                        .doOnNext { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onNext = { view?.setSpendingList(it) }
                        )
        )
    }
}