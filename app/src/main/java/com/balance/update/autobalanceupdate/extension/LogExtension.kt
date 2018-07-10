package com.balance.update.autobalanceupdate.extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

fun toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun toastUI(context: Context, text: String) {
    val mainHandler = Handler(Looper.getMainLooper())

    mainHandler.post {
        toast(context, text)
    }
}

fun logd(text: String?) {
    Log.d("logoff", text ?: "null")
}

fun loge(ex: Exception) {
    Log.e("logoff", ex.message, ex)
}