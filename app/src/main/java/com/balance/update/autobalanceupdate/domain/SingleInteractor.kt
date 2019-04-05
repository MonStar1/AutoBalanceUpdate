package com.balance.update.autobalanceupdate.domain

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class SingleInteractor<T, Params> {

    fun execute(params: Params): Single<T> {
        return buildCase(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    protected abstract fun buildCase(params: Params): Single<T>
}