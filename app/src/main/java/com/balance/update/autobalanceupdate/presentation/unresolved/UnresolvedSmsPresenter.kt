package com.balance.update.autobalanceupdate.presentation.unresolved

import com.balance.update.autobalanceupdate.domain.filter.SubscribeFilters
import com.balance.update.autobalanceupdate.domain.unresolved.*
import com.balance.update.autobalanceupdate.presentation.BasePresenter
import com.balance.update.autobalanceupdate.presentation.MvpView
import com.balance.update.autobalanceupdate.presentation.entities.SelectedPattern
import com.balance.update.autobalanceupdate.presentation.entities.UnresolvedSmsCard
import io.reactivex.rxkotlin.subscribeBy

interface UnresolvedSmsView : MvpView {
    fun setUnresolvedSms(list: List<UnresolvedSmsCard>)
}

class UnresolvedSmsPresenter : BasePresenter<UnresolvedSmsView>() {
    private val getUnresolvedSms = SubscribeUnresolvedSms()
    private val getFilters = SubscribeFilters()
    private val createSmsPattern = CreateSmsPattern()
    private val resolveNewSms = ResolveNewSms()

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

    fun applyFilterTo(selectedPattern: SelectedPattern) {
        view?.showProgress(true)

        disposable.add(
                createSmsPattern.execute(CreateSmsPatternInput(selectedPattern.filter, selectedPattern.sender, selectedPattern.bodyPattern, selectedPattern.unresolvedSms))
                        .doAfterTerminate { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onSuccess = {}
                        )
        )
    }

}