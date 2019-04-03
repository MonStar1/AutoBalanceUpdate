package com.balance.update.autobalanceupdate.filter.presentation

interface Presenter<T : MvpView> {
    fun onViewAttached(view: T)
    fun onViewDetached()
}