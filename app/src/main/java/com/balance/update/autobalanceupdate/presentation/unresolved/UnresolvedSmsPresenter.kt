package com.balance.update.autobalanceupdate.presentation.unresolved

import com.balance.update.autobalanceupdate.domain.filter.GetFilters
import com.balance.update.autobalanceupdate.domain.unresolved.GetUnresolvedSms
import com.balance.update.autobalanceupdate.presentation.BasePresenter
import com.balance.update.autobalanceupdate.presentation.MvpView
import com.balance.update.autobalanceupdate.presentation.entities.UnresolvedSmsCard
import io.reactivex.rxkotlin.subscribeBy

interface UnresolvedSmsView : MvpView {
    fun setUnresolvedSms(list: List<UnresolvedSmsCard>)
}

class UnresolvedSmsPresenter : BasePresenter<UnresolvedSmsView>() {
    private val getUnresolvedSms = GetUnresolvedSms()
    private val getFilters = GetFilters()

    override fun onViewAttached(view: UnresolvedSmsView) {
        super.onViewAttached(view)

        subscribeUnresolvedSms()
    }

    private fun subscribeUnresolvedSms() {
        view?.showProgress(true)

        disposable.add(
                getFilters.execute(Unit)
                        .flatMap { listFilter ->
                            getUnresolvedSms.execute(Unit)
                                    .map { listSms ->
                                        listSms.map { UnresolvedSmsCard(it, listFilter) }
                                    }
                        }
                        .doAfterNext { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onNext = { view?.setUnresolvedSms(it) }
                        )

        )
    }

}