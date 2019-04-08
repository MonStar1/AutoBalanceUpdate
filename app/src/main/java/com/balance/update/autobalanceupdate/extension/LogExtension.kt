package com.balance.update.autobalanceupdate.extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

fun toast(context: Context, text: String, duration: Int = Toast.LENGTH_SHORT) {
    val mainHandler = Handler(Looper.getMainLooper())

    mainHandler.post {
        Toast.makeText(context, text, duration).show()
    }
}

fun toast(context: Context, error: Throwable) {
    toast(context, error.localizedMessage, Toast.LENGTH_LONG)
}

fun logd(text: String?) {
    Log.d("logoff", text ?: "null")
}

fun loge(ex: Throwable) {
    Log.e("Exception_logoff", ex.message, ex)
}