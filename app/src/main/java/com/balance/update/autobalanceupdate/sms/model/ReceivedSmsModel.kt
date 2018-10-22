package com.balance.update.autobalanceupdate.sms.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReceivedSmsModel(val sender: String, val message: String) : Parcelable {
    companion object {
        const val EXTRA_SENDER = "EXTRA_SENDER"
        const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
    }
}