package com.balance.update.autobalanceupdate.extension

import android.content.Context
import android.util.Log
import android.widget.Toast

fun toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun logd(text: String?) {
    Log.d("exce", text ?: "null")
}

fun loge(ex: Exception) {
    Log.e("exce", ex.message, ex)
}