package com.balance.update.autobalanceupdate.presentation

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class BasePresenterActivity<V : MvpView> : AppCompatActivity() {
    abstract val presenter: Presenter<V>
    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        doOnCreate()

        presenter.onViewAttached(this as V)
    }

    abstract fun doOnCreate()

    override fun onDestroy() {
        super.onDestroy()

        presenter.onViewDetached()
    }
}