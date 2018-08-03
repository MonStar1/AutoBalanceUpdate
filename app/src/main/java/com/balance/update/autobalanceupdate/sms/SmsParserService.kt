package com.balance.update.autobalanceupdate.sms

import android.app.IntentService
import android.content.Intent
import android.provider.Telephony
import com.balance.update.autobalanceupdate.GoogleServiceAuth
import com.balance.update.autobalanceupdate.extension.toastUI
import com.balance.update.autobalanceupdate.sheets.SheetsApi
import com.balance.update.autobalanceupdate.sms.parser.SmsData
import com.balance.update.autobalanceupdate.sms.parser.SmsParseException
import com.balance.update.autobalanceupdate.sms.parser.SmsSenderException
import com.balance.update.autobalanceupdate.sms.seller.Seller
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.model.UpdateValuesResponse

class SmsParserService : IntentService("SmsService") {

    companion object {
        const val HALVA_BALANCE_CELL = "C6"
        const val PRIOR_BALANCE_CELL = "C5"
        const val FOOD_CELL = "C9"
        const val BALANCE_SPREADSHEET = "15NfMZvT2qDM8Xja1GnqumkNd8sIEgDM2XbMNaWkJocQ"
        const val BALANCE_SHEET = "Sheet_1"
    }


    override fun onHandleIntent(intent: Intent) {
        Telephony.Sms.Intents.getMessagesFromIntent(intent).forEach {
            handleMessage(it.originatingAddress, it.messageBody)
        }
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
        } catch (ex: SmsSenderException) {
            toastUI(this, "Unknown sms sender: $sender")
            return
        }


        val sheetsApi = SheetsApi(googleAccountCredential).apply {
            selectSpreadsheetId(BALANCE_SPREADSHEET)
            selectSheet(BALANCE_SHEET)
        }

        var result = UpdateValuesResponse()

        result.updatedRows = -999

        result = when (smsData.sender) {
            is SmsSender.Mtbank -> sheetsApi.updateCell(HALVA_BALANCE_CELL, smsData.actualBalance)
            is SmsSender.PriorBank -> sheetsApi.updateCell(PRIOR_BALANCE_CELL, smsData.actualBalance)
            is SmsSender.Test -> result
        }

        toastUI(this, "updated: ${result.updatedRows}")

        when (smsData.seller) {
            is Seller.Food -> {
                val balance = sheetsApi.readCell(FOOD_CELL).toDouble()
                val newBalance = balance - smsData.spent

                sheetsApi.updateCell(FOOD_CELL, newBalance)
            }
        }
    }

}