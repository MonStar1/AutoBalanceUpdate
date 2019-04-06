package com.balance.update.autobalanceupdate.domain

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class MaybeInteractor<T, Params> {

    fun execute(params: Params): Maybe<T> {
        return buildCase(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    protected abstract fun buildCase(params: Params): Maybe<T>
}