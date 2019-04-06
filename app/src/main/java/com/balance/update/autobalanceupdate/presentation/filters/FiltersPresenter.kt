package com.balance.update.autobalanceupdate.presentation.filters

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.domain.filter.CreateFilter
import com.balance.update.autobalanceupdate.domain.filter.DeleteFilter
import com.balance.update.autobalanceupdate.domain.filter.SubscribeFilters
import com.balance.update.autobalanceupdate.domain.filter.UpdateFilter
import com.balance.update.autobalanceupdate.presentation.BasePresenter
import com.balance.update.autobalanceupdate.presentation.MvpView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

interface FilterView : MvpView {
    fun setFilters(arrayOfFilters: List<Filter>)

}

class FiltersPresenter : BasePresenter<FilterView>() {
    private val getFilters = SubscribeFilters()
    private val createFilter = CreateFilter()
    private val deleteFilter = DeleteFilter()
    private val updateFilter = UpdateFilter()

    override fun onViewAttached(view: FilterView) {
        super.onViewAttached(view)

        subscribeFilters()
    }

    private fun subscribeFilters() {
        view?.showProgress(true)

        disposable.add(
                getFilters.execute(Unit)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterNext { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onNext = { view?.setFilters(it) }
                        )
        )
    }

    fun createNewFilter(filterName: String) {
        view?.showProgress(true)

        disposable.add(
                createFilter.execute(filterName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) },
                                onSuccess = {}
                        )
        )
    }
}