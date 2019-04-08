package com.balance.update.autobalanceupdate.domain

import io.reactivex.Observable
import io.reactivex.Scheduler

private class ObservableDispatcher<T> : Dispatcher<Observable<T>> {

    override fun observeOn(scheduler: Scheduler, source: Observable<T>): Observable<T> =
            source.observeOn(scheduler)

    override fun subscribeOn(scheduler: Scheduler, source: Observable<T>): Observable<T> =
            source.subscribeOn(scheduler)
}


abstract class ObservableInteractor<Output, Params> : Interactor<Observable<Output>, Params>(ObservableDispatcher())