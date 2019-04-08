package com.balance.update.autobalanceupdate.domain

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface Dispatcher<Source> {
    fun observeOn(scheduler: Scheduler, source: Source): Source
    fun subscribeOn(scheduler: Scheduler, source: Source): Source
}

abstract class Interactor<Source, Params>(private val dispatcher: Dispatcher<Source>) {

    fun execute(params: Params): Source {
        return buildCase(params)
                .run { dispatcher.subscribeOn(Schedulers.io(), this) }
                .run { dispatcher.observeOn(AndroidSchedulers.mainThread(), this) }
    }

    fun attach(params: Params): Source {
        return buildCase(params)
    }

    protected abstract fun buildCase(params: Params): Source
}