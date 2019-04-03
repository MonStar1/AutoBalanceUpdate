package com.balance.update.autobalanceupdate.filter.presentation

interface MvpView {
    fun onError(error: Throwable)
    fun showProgress(isVisible: Boolean)
}