package com.balance.update.autobalanceupdate.sms

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import com.balance.update.autobalanceupdate.GoogleServiceAuth
import com.balance.update.autobalanceupdate.extension.logd
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.extension.toastUI
import com.balance.update.autobalanceupdate.sheets.SheetsApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.model.UpdateValuesResponse

class SmsParserService : IntentService("SmsService") {

    companion object {
        const val HALVA_BALANCE_CELL = "C6"
        const val PRIOR_BALANCE_CELL = "C5"
        const val BALANCE_SPREADSHEET = "15NfMZvT2qDM8Xja1GnqumkNd8sIEgDM2XbMNaWkJocQ"
        const val BALANCE_SHEET = "Sheet_1"
    }

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
            handleMessage(it.originatingAddress, it.messageBody)
        }

        toastUI(this, "service onHandleIntent")
    }

    private fun handleMessage(sender: String, messageBody: String) {
        toastUI(this, "message: $messageBody")

        val googleAccountCredential = GoogleAccountCredential
                .usingOAuth2(this, GoogleServiceAuth.SCOPES)
                .setBackOff(ExponentialBackOff())

        googleAccountCredential?.selectedAccount = GoogleSignIn.getLastSignedInAccount(this)?.account

        val smsData: SmsData

        try {
            smsData = SmsParserFactory(sender, messageBody).getParser().parse()
        } catch (ex: SmsParseException) {
            toastUI(this, "Unknown sms type: $messageBody")
            return
        }


        val sheetsApi = SheetsApi(googleAccountCredential).apply {
            selectSpreadsheetId(BALANCE_SPREADSHEET)
            selectSheet(BALANCE_SHEET)
        }

        val result: UpdateValuesResponse

        result = when (smsData.sender) {
            is SmsSender.Mtbank -> sheetsApi.updateCell(HALVA_BALANCE_CELL, smsData.actualBalance)
            is SmsSender.PriorBank -> sheetsApi.updateCell(PRIOR_BALANCE_CELL, smsData.actualBalance)
        }

        toastUI(this, "updated: ${result.updatedRows}")
    }

}