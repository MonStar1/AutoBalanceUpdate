package com.balance.update.autobalanceupdate.domain

import io.reactivex.Maybe
import io.reactivex.Scheduler

private class MaybeDispatcher<T> : Dispatcher<Maybe<T>> {

    override fun observeOn(scheduler: Scheduler, source: Maybe<T>): Maybe<T> =
            source.observeOn(scheduler)

    override fun subscribeOn(scheduler: Scheduler, source: Maybe<T>): Maybe<T> =
            source.subscribeOn(scheduler)
}


abstract class MaybeInteractor<Output, Params> : Interactor<Maybe<Output>, Params>(MaybeDispatcher())