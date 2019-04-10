package com.balance.update.autobalanceupdate.presentation.filters

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.memory.DateRange
import com.balance.update.autobalanceupdate.domain.filter.*
import com.balance.update.autobalanceupdate.presentation.BasePresenter
import com.balance.update.autobalanceupdate.presentation.MvpView
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

interface FilterView : MvpView {
    fun setFilters(arrayOfFilters: List<Filter>)
    fun setDateRange(dateRange: DateRange)
}

class FiltersPresenter : BasePresenter<FilterView>() {
    private val getDateRange = GetDateRange()
    private val setDateRange = SetDateRange()
    private val getFilters = SubscribeFiltersByRangeDate()
    private val createFilter = CreateFilter()
    private val deleteFilter = DeleteFilter()
    private val updateFilter = UpdateFilter()

    private var subscribeFilterDisposable: Disposable? = null

    override fun onViewAttached(view: FilterView) {
        super.onViewAttached(view)

        subscribeFilters()
    }

    private fun subscribeFilters() {
        view?.showProgress(true)

        subscribeFilterDisposable?.dispose()

        subscribeFilterDisposable = getDateRange.execute(Unit)
                .doOnSuccess {
                    view?.setDateRange(it)
                }
                .flatMapObservable {
                    getFilters.execute(it)
                }
                .doAfterNext { view?.showProgress(false) }
                .subscribeBy(
                        onError = { view?.onError(it) },
                        onNext = { view?.setFilters(it) }
                )

        disposable.add(
                subscribeFilterDisposable!!
        )
    }

    fun createNewFilter(filterName: String) {
        view?.showProgress(true)

        disposable.add(
                createFilter.execute(filterName)
                        .doAfterTerminate { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) }
                        )
        )
    }

    fun deleteFilter(filter: Filter) {
        view?.showProgress(true)

        disposable.add(
                deleteFilter.execute(filter)
                        .doAfterTerminate { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) }
                        )
        )
    }

    fun updateFilter(filter: Filter) {
        view?.showProgress(true)

        disposable.add(
                updateFilter.execute(filter)
                        .doAfterTerminate { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onSuccess = {}
                        )
        )
    }

    fun setSelectedDateRange(startDateInMillis: Long, endDateInMillis: Long) {
        disposable.add(
                setDateRange.execute(DateRange(startDateInMillis, endDateInMillis))
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onComplete = { subscribeFilters() }
                        )
        )
    }
}