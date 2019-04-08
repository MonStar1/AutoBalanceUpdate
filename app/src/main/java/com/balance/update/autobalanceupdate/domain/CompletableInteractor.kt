package com.balance.update.autobalanceupdate.domain

import io.reactivex.Completable
import io.reactivex.Scheduler

private class CompletableDispatcher : Dispatcher<Completable> {

    override fun observeOn(scheduler: Scheduler, source: Completable): Completable =
            source.observeOn(scheduler)

    override fun subscribeOn(scheduler: Scheduler, source: Completable): Completable =
            source.subscribeOn(scheduler)
}


abstract class CompletableInteractor<Params> : Interactor<Completable, Params>(CompletableDispatcher())