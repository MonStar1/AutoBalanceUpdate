package com.balance.update.autobalanceupdate.presentation

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.balance.update.autobalanceupdate.R


abstract class BasePresenterActivity<V : MvpView> : AppCompatActivity(), MvpView {
    abstract val presenter: Presenter<V>
    @get:LayoutRes
    abstract val layoutId: Int
    private var progressDialog: AlertDialog? = null

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

    final override fun showProgress(isVisible: Boolean) {
        if(isVisible) showLoader() else hideLoader()
    }

    private fun showLoader() {
        if (isDestroyed || isFinishing) return
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing()) {
                progressDialog!!.show()
            }
        } else {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null, false)
            progressDialog = AlertDialog.Builder(this, R.style.TransparentAlertDialog)
                    .setView(view)
                    .setCancelable(false)
                    .show()
        }
    }

    private fun hideLoader() {
        if (isDestroyed || isFinishing) return
        if (progressDialog != null && progressDialog!!.isShowing()) {
            progressDialog!!.dismiss()
        }
    }
}