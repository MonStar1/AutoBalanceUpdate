package com.balance.update.autobalanceupdate.presentation

interface Presenter<T : MvpView> {
    fun onViewAttached(view: T)
    fun onViewDetached()
}