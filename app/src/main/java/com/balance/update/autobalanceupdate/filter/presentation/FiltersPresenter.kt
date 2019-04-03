package com.balance.update.autobalanceupdate.filter.presentation

import com.balance.update.autobalanceupdate.filter.data.entities.Filter
import com.balance.update.autobalanceupdate.filter.data.repository.FilterRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

interface FilterView : MvpView {
    fun setFilters(arrayOfFilters: List<Filter>)

}

class FiltersPresenter : BasePresenter<FilterView>() {
    private val repository = FilterRepository()

    override fun onViewAttached(view: FilterView) {
        super.onViewAttached(view)

        subscribeFilters()
    }

    private fun subscribeFilters() {
        view?.showProgress(true)

        disposable.add(
                repository.loadAll()
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
                repository.create(filterName)
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
                repository.delete(filter)
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
                repository.update(filter)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate { view?.showProgress(false) }
                        .subscribeBy(
                                onError = { view?.onError(it) }
                        )
        )
    }
}