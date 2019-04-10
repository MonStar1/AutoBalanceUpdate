package com.balance.update.autobalanceupdate.data.services.sms

import android.app.IntentService
import android.content.Intent
import android.provider.Telephony
import com.balance.update.autobalanceupdate.domain.unresolved.LoadUnresolvedSms
import com.balance.update.autobalanceupdate.domain.unresolved.ResolveNewSms
import com.balance.update.autobalanceupdate.domain.unresolved.ResolveSmsInput
import com.balance.update.autobalanceupdate.extension.loge
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.widget.UnresolvedSmsNotification
import io.reactivex.rxkotlin.subscribeBy

class SmsParserService : IntentService("SmsService") {

    companion object {
        const val HALVA_BALANCE_CELL = "C6"
        const val PRIOR_BALANCE_CELL = "C5"
        const val FOOD_CELL = "C9"
        const val BALANCE_SPREADSHEET = "15NfMZvT2qDM8Xja1GnqumkNd8sIEgDM2XbMNaWkJocQ"
        const val BALANCE_SHEET = "Sheet_1"
    }

    private val notification = UnresolvedSmsNotification(this)

    override fun onHandleIntent(intent: Intent) {
        Telephony.Sms.Intents.getMessagesFromIntent(intent).forEach {
            handleMessage(it.originatingAddress, it.messageBody, System.currentTimeMillis())
        }
    }

    private fun handleMessage(sender: String, messageBody: String, timestampMillis: Long) {
        if (SmsSender.values().none { it.value == sender }) {
            return
        }

        val disposable = ResolveNewSms().execute(ResolveSmsInput(sender, messageBody, timestampMillis))
                .onErrorResumeNext {
                    LoadUnresolvedSms().execute(Unit)
                            .doOnSuccess {
                                notification.show(it.size)
                            }.ignoreElement()
                }
                .subscribeBy(
                        onError = {
                            toast(this@SmsParserService, "Please, create new filter for message: $messageBody")
                            toast(this@SmsParserService, it)
                            loge(it)
                        },
                        onComplete = {
                            toast(this@SmsParserService, "Attached to filter")
                        }
                )

//        val googleAccountCredential = GoogleAccountCredential
//                .usingOAuth2(this, GoogleServiceAuth.SCOPES)
//                .setBackOff(ExponentialBackOff())
//
//        googleAccountCredential?.selectedAccount = GoogleSignIn.getLastSignedInAccount(this)?.account
//
//        val smsData: SmsData
//
//        try {
//            smsData = SmsParserFactory(sender, messageBody).getParser().parse()
//        } catch (ex: SmsParseException) {
//            toast(this, "Unknown sms type: $messageBody")
//            return
//        } catch (ex: SmsSenderException) {
//            toast(this, "Unknown sms sender: $sender")
//            return
//        }
//
//
//        val sheetsApi = SheetsApi(googleAccountCredential).apply {
//            selectSpreadsheetId(BALANCE_SPREADSHEET)
//            selectSheet(BALANCE_SHEET)
//        }
//
//        var result = UpdateValuesResponse()
//
//        result.updatedRows = -999
//
//        result = when (smsData.sender) {
//            is SmsSender.Mtbank -> sheetsApi.updateCell(HALVA_BALANCE_CELL, smsData.actualBalance)
//            is SmsSender.PriorBank -> sheetsApi.updateCell(PRIOR_BALANCE_CELL, smsData.actualBalance)
//            is SmsSender.Test -> result
//        }
//
//        toast(this, "updated: ${result.updatedRows}")
//
//        when (smsData.seller) {
//            is Seller.Food -> {
//                val balance = sheetsApi.readCell(FOOD_CELL).toDouble()
//                val newBalance = balance - smsData.spent.amount
//
//                sheetsApi.updateCell(FOOD_CELL, newBalance)
//            }
//        }
    }

}