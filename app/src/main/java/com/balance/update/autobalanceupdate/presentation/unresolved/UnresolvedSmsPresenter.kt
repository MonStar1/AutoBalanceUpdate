package com.balance.update.autobalanceupdate.presentation.unresolved

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms
import com.balance.update.autobalanceupdate.domain.filter.GetFilters
import com.balance.update.autobalanceupdate.domain.unresolved.CreateSmsPattern
import com.balance.update.autobalanceupdate.domain.unresolved.CreateSmsPatternInput
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
    private val createSmsPattern = CreateSmsPattern()

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

    fun applyFilterTo(filter: Filter, unresolvedSms: UnresolvedSms) {
        view?.showProgress(true)

        disposable.add(
                createSmsPattern.execute(CreateSmsPatternInput(filter, unresolvedSms.sender, unresolvedSms.body, unresolvedSms))
                        .doAfterTerminate { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onSuccess = {}
                        )
        )
    }

}