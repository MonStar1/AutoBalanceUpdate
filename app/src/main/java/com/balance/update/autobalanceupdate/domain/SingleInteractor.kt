package com.balance.update.autobalanceupdate.domain

import io.reactivex.Scheduler
import io.reactivex.Single

private class SingleDispatcher<T> : Dispatcher<Single<T>> {

    override fun observeOn(scheduler: Scheduler, source: Single<T>): Single<T> =
            source.observeOn(scheduler)

    override fun subscribeOn(scheduler: Scheduler, source: Single<T>): Single<T> =
            source.subscribeOn(scheduler)
}


abstract class SingleInteractor<Output, Params> : Interactor<Single<Output>, Params>(SingleDispatcher())