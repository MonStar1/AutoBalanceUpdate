package com.balance.update.autobalanceupdate.presentation

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<T : MvpView> : Presenter<T> {

    protected val disposable = CompositeDisposable()

    var view: T? = null

    override fun onViewAttached(view: T) {
        this.view = view
    }

    override fun onViewDetached() {
        disposable.dispose()
        view = null
    }
}