package com.balance.update.autobalanceupdate.sms

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import com.balance.update.autobalanceupdate.GoogleServiceAuth
import com.balance.update.autobalanceupdate.extension.logd
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.sheets.SheetsApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff

class SmsParserService : IntentService("SmsService") {

    override fun onCreate() {
        super.onCreate()

        logd("service created")
    }

    override fun onDestroy() {
        super.onDestroy()

        logd("service destroyed")

    }

    override fun onHandleIntent(intent: Intent) {
        Telephony.Sms.Intents.getMessagesFromIntent(intent).forEach {

            if (handleNumber(it.originatingAddress)) {
                handleMessage(it.messageBody)
            }
        }

        logd("service onHandleIntent")
    }

    private fun handleMessage(messageBody: String?) {
        logd("message: $messageBody")

        val ostatok = "OSTATOK "
        val byn = " BYN"

        val lastIndexOfOstatok = messageBody?.lastIndexOf(ostatok)!! + ostatok.length
        val lastIndexOfByn = messageBody?.lastIndexOf(byn)

        val halvaValue = messageBody.substring(lastIndexOfOstatok..lastIndexOfByn).toDouble()

        val googleAccountCredential = GoogleAccountCredential
                .usingOAuth2(this, GoogleServiceAuth.SCOPES)
                .setBackOff(ExponentialBackOff())

        googleAccountCredential?.selectedAccount = GoogleSignIn.getLastSignedInAccount(this)?.account

        val result = SheetsApi().updateSheet(googleAccountCredential, halvaValue)

        if (result != null) {
            val mainHandler = Handler(Looper.getMainLooper())

            mainHandler.post(Runnable {
                // Do your stuff here related to UI, e.g. show toast
                toast(this, "result: ${result.updatedRows}")
            })
        }
    }

    private fun handleNumber(originatingAddress: String?): Boolean {
        return originatingAddress?.contains("MTBANK", ignoreCase = true) ?: false
    }

}