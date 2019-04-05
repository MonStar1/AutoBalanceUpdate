package com.balance.update.autobalanceupdate.presentation

interface MvpView {
    fun onError(error: Throwable)
    fun showProgress(isVisible: Boolean)
}