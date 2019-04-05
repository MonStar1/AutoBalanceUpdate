package com.balance.update.autobalanceupdate.domain

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class ObservableInteractor<T, Params> {

    fun execute(params: Params): Observable<T> {
        return buildCase(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    protected abstract fun buildCase(params: Params): Observable<T>
}