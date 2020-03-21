package com.balance.update.autobalanceupdate.sms

import android.app.IntentService
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast
import com.balance.update.autobalanceupdate.App
import com.balance.update.autobalanceupdate.GoogleServiceAuth
import com.balance.update.autobalanceupdate.extension.toastUI
import com.balance.update.autobalanceupdate.room.LogEntity
import com.balance.update.autobalanceupdate.sheets.SheetsApi
import com.balance.update.autobalanceupdate.sms.parser.SmsData
import com.balance.update.autobalanceupdate.sms.parser.SmsParseException
import com.balance.update.autobalanceupdate.sms.parser.SmsSenderException
import com.balance.update.autobalanceupdate.sms.seller.Seller
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class SmsParserService : IntentService("SmsService") {

    companion object {
        const val HALVA_BALANCE_CELL = "C6"
        const val PRIOR_BALANCE_CELL = "C5"
        const val FOOD_CELL = "C9"
        const val HEALTH_CELL = "C10"
        const val TRANSPORT_CELL = "C11"
        const val SWEET_CELL = "C13"
        const val CAFE_CELL = "C15"
        const val HOUSEHOLD_CELL = "C17"
        const val CLOTHES_CELL = "C18"
        const val CHILD_CELL = "C19"
        const val CHANGED_USD_CELL = "C3"
        const val TO_SPEND_USD_CELL = "C4"
        const val CASH_CELL = "C7"
        const val ON_THE_CARD_USD_CELL = "B37"
        const val BALANCE_SPREADSHEET = "15NfMZvT2qDM8Xja1GnqumkNd8sIEgDM2XbMNaWkJocQ"
        const val BALANCE_SHEET = "Sheet_1"
    }


    override fun onHandleIntent(intent: Intent) {
        Telephony.Sms.Intents.getMessagesFromIntent(intent).forEach {
            try {
                handleMessage(it.originatingAddress, it.messageBody)
            } catch (ex: Throwable) {
                Toast.makeText(this, "Some excepion: ${ex.localizedMessage}", Toast.LENGTH_LONG).show()
            }
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

        when (smsData) {
            is SmsData.SmsSpent -> resolveSmsSpent(result, smsData, sheetsApi)
            is SmsData.SmsExchange -> resolveSmsExchange(smsData, sheetsApi)
            is SmsData.SmsGetCash -> resolveSmsGetCash(smsData, sheetsApi)
        }

    }

    private fun resolveSmsGetCash(smsData: SmsData.SmsGetCash, sheetsApi: SheetsApi) {
        toastUI(this, "Снятие наличных BYN ${smsData.cashBYN}")

        when (smsData.sender) {
            is SmsSender.PriorBank -> {
                val cashBYN = sheetsApi.readCell(CASH_CELL).toDouble()

                sheetsApi.updateCell(PRIOR_BALANCE_CELL, smsData.actualBalance)
                sheetsApi.updateCell(CASH_CELL, cashBYN + smsData.cashBYN)

                App.db.logDao()
                        .insert(LogEntity(sender = smsData.sender.name, seller = "Банкомат",
                                actualBalance = smsData.actualBalance, spent = 0.0,
                                categoryBalance = 0.0, sellerText = "Снятие наличных BYN ${smsData.cashBYN}"))
                        .subscribeOn(Schedulers.io())
                        .subscribe()
            }
            is SmsSender.Mtbank -> {
            }
            is SmsSender.Test -> {
            }
        }
    }

    private fun resolveSmsSpent(result: UpdateValuesResponse, smsSpent: SmsData.SmsSpent, sheetsApi: SheetsApi) {
        var result1 = result
        result1 = when (smsSpent.sender) {
            is SmsSender.Mtbank -> sheetsApi.updateCell(HALVA_BALANCE_CELL, smsSpent.actualBalance)
            is SmsSender.PriorBank -> sheetsApi.updateCell(PRIOR_BALANCE_CELL, smsSpent.actualBalance)
            is SmsSender.Test -> result1
        }

        toastUI(this, "updated: ${result1.updatedRows}")

        var balanceCell = 0.0
        when (smsSpent.seller) {
            is Seller.Food -> {
                val balance = sheetsApi.readCell(FOOD_CELL).toDouble()
                balanceCell = balance - smsSpent.spent

                sheetsApi.updateCell(FOOD_CELL, balanceCell)
            }
            is Seller.Health -> {
                val balance = sheetsApi.readCell(HEALTH_CELL).toDouble()
                balanceCell = balance - smsSpent.spent

                sheetsApi.updateCell(HEALTH_CELL, balanceCell)
            }
            is Seller.Transport -> {
                val balance = sheetsApi.readCell(TRANSPORT_CELL).toDouble()
                balanceCell = balance - smsSpent.spent

                sheetsApi.updateCell(TRANSPORT_CELL, balanceCell)
            }
            is Seller.Sweet -> {
                val balance = sheetsApi.readCell(SWEET_CELL).toDouble()
                balanceCell = balance - smsSpent.spent

                sheetsApi.updateCell(SWEET_CELL, balanceCell)
            }
            is Seller.Cafe -> {
                val balance = sheetsApi.readCell(CAFE_CELL).toDouble()
                balanceCell = balance - smsSpent.spent

                sheetsApi.updateCell(CAFE_CELL, balanceCell)
            }
            is Seller.Household -> {
                val balance = sheetsApi.readCell(HOUSEHOLD_CELL).toDouble()
                balanceCell = balance - smsSpent.spent

                sheetsApi.updateCell(HOUSEHOLD_CELL, balanceCell)
            }
            is Seller.Clothes -> {
                val balance = sheetsApi.readCell(CLOTHES_CELL).toDouble()
                balanceCell = balance - smsSpent.spent

                sheetsApi.updateCell(CLOTHES_CELL, balanceCell)
            }
            is Seller.Child -> {
                val balance = sheetsApi.readCell(CHILD_CELL).toDouble()
                balanceCell = balance - smsSpent.spent

                sheetsApi.updateCell(CHILD_CELL, balanceCell)
            }
        }

        App.db.logDao()
                .insert(LogEntity(sender = smsSpent.sender.name, seller = smsSpent.seller.name,
                        actualBalance = smsSpent.actualBalance, spent = smsSpent.spent,
                        categoryBalance = balanceCell, sellerText = "Spent"))
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun resolveSmsExchange(smsExchange: SmsData.SmsExchange, sheetsApi: SheetsApi) {
        toastUI(this, "Perevod USD $ ${smsExchange.exchangedUSD}")

        when (smsExchange.sender) {
            is SmsSender.PriorBank -> {
                val changedUsd = sheetsApi.readCell(CHANGED_USD_CELL).toDouble()
                val toSpendUsd = sheetsApi.readCell(TO_SPEND_USD_CELL).toDouble()
                val onCardUsd = sheetsApi.readCell(ON_THE_CARD_USD_CELL).toDouble()

                sheetsApi.updateCell(PRIOR_BALANCE_CELL, smsExchange.actualBalance)
                sheetsApi.updateCell(CHANGED_USD_CELL, changedUsd + smsExchange.exchangedUSD)
                sheetsApi.updateCell(TO_SPEND_USD_CELL, toSpendUsd - smsExchange.exchangedUSD)
                sheetsApi.updateCell(ON_THE_CARD_USD_CELL, onCardUsd - smsExchange.exchangedUSD)

                App.db.logDao()
                        .insert(LogEntity(sender = smsExchange.sender.name, seller = "Перевод",
                                actualBalance = smsExchange.actualBalance, spent = 0.0,
                                categoryBalance = 0.0, sellerText = "Perevod USD $ ${smsExchange.exchangedUSD}"))
                        .subscribeOn(Schedulers.io())
                        .subscribe()
            }
            is SmsSender.Mtbank -> {
            }
            is SmsSender.Test -> {
            }
        }

    }

}