package com.balance.update.autobalanceupdate.sms

import androidx.datastore.preferences.edit
import com.balance.update.autobalanceupdate.App
import com.balance.update.autobalanceupdate.GoogleServiceAuth
import com.balance.update.autobalanceupdate.extension.toastUI
import com.balance.update.autobalanceupdate.room.LogEntity
import com.balance.update.autobalanceupdate.sheets.SheetsApi
import com.balance.update.autobalanceupdate.sms.SmsSender.PriorBank
import com.balance.update.autobalanceupdate.sms.parser.SmsData
import com.balance.update.autobalanceupdate.sms.category.Category
import com.balance.update.autobalanceupdate.sms.category.Category.Unknown
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking

class SmsResolver(val app: App) {

    var sheetsApi: SheetsApi

    init {
        val googleAccountCredential = GoogleAccountCredential
            .usingOAuth2(app, GoogleServiceAuth.SCOPES)
            .setBackOff(ExponentialBackOff())

        googleAccountCredential?.selectedAccount = GoogleSignIn.getLastSignedInAccount(app)?.account

        sheetsApi = SheetsApi(googleAccountCredential).apply {
            selectSpreadsheetId(BALANCE_SPREADSHEET)
            selectSheet(BALANCE_SHEET)
        }
    }

    companion object {

        const val HALVA_BALANCE_CELL = "C6"
        const val PRIOR_BALANCE_CELL = "C5"
        const val FOOD_CELL = "C9"
        const val HEALTH_CELL = "C10"
        const val TRANSPORT_CELL = "C11"
        const val SWEET_CELL = "C12"
        const val CAFE_CELL = "C13"
        const val HOUSEHOLD_CELL = "C17"
        const val CLOTHES_CELL = "C14"
        const val CHILD_CELL = "C15"
        const val MUSIC_CELL = "C16"
        const val FUN_CELL = "C18"
        const val GIFT_CELL = "C19"
        const val UNEXPECTED_CELL = "C20"
        const val CHANGED_USD_CELL = "C3"
        const val TO_SPEND_USD_CELL = "C4"
        const val CASH_CELL = "F8"
        const val UNKNOWN_SELLER = "C21"
        const val ON_THE_CARD_USD_CELL = "B38"
        const val BALANCE_SPREADSHEET = "15NfMZvT2qDM8Xja1GnqumkNd8sIEgDM2XbMNaWkJocQ"
        const val BALANCE_SHEET = "Sheet_1"
    }

    fun resolve(smsData: SmsData, timeMillis: Long) {
        when (smsData) {
            is SmsData.SmsSpent -> resolveSmsSpent(smsData, sheetsApi, timeMillis)
            is SmsData.SmsExchange -> resolveSmsExchange(smsData, sheetsApi, timeMillis)
            is SmsData.SmsGetCash -> resolveSmsGetCash(smsData, sheetsApi, timeMillis)
        }
    }

    private fun resolveSmsGetCash(smsData: SmsData.SmsGetCash, sheetsApi: SheetsApi, timeMillis: Long) {
        toastUI(app, "Снятие наличных BYN ${smsData.cashBYN}")

        when (smsData.sender) {
            is PriorBank -> {
                val cashBYN = sheetsApi.readCell(CASH_CELL).toDouble()

                sheetsApi.updateCell(PRIOR_BALANCE_CELL, smsData.actualBalance)
                sheetsApi.updateCell(CASH_CELL, cashBYN + smsData.cashBYN)

                runBlocking {
                    app.datastore.edit {
                        if (smsData.sender is PriorBank) {
                            it[app.priorBalance] = smsData.actualBalance.toLong()
                        } else {
                            it[app.mtbankBalance] = smsData.actualBalance.toLong()
                        }
                    }
                }

                App.db.logDao()
                    .insert(
                        LogEntity(
                            sender = smsData.sender.name, seller = "Банкомат",
                            actualBalance = smsData.actualBalance, spent = 0.0,
                            categoryBalance = 0.0, sellerText = "Снятие наличных BYN ${smsData.cashBYN}",
                            timeInMillis = timeMillis, isSellerResolved = true
                        )
                    )
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
            is SmsSender.Mtbank -> {
            }
            is SmsSender.Test -> {
            }
        }
    }

    private fun resolveSmsSpent(smsSpent: SmsData.SmsSpent, sheetsApi: SheetsApi, timeMillis: Long) {
        when (smsSpent.sender) {
            is SmsSender.Mtbank -> sheetsApi.updateCell(HALVA_BALANCE_CELL, smsSpent.actualBalance)
            is PriorBank -> sheetsApi.updateCell(
                PRIOR_BALANCE_CELL,
                smsSpent.actualBalance
            )
            is SmsSender.Test -> Unit
        }

        val balanceCell = resolveSeller(smsSpent.spent, smsSpent.category, false)

        val categoryName = if (smsSpent.category is Unknown) smsSpent.sellerName else smsSpent.category.name

        runBlocking {
            app.datastore.edit {
                if (smsSpent.sender is PriorBank) {
                    it[app.priorBalance] = smsSpent.actualBalance.toLong()
                } else {
                    it[app.mtbankBalance] = smsSpent.actualBalance.toLong()
                }
            }
        }

        App.db.logDao()
            .insert(
                LogEntity(
                    sender = smsSpent.sender.name, seller = categoryName,
                    actualBalance = smsSpent.actualBalance, spent = smsSpent.spent,
                    categoryBalance = balanceCell, sellerText = smsSpent.sellerName,
                    timeInMillis = timeMillis, isSellerResolved = smsSpent.category !is Unknown
                )
            )
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun resolveSeller(spent: Double, category: Category, isFixResolve: Boolean): Double {
        var balanceCell = 0.0
        when (category) {
            is Category.Food -> {
                val balance = sheetsApi.readCell(FOOD_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(FOOD_CELL, balanceCell)
            }
            is Category.Health -> {
                val balance = sheetsApi.readCell(HEALTH_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(HEALTH_CELL, balanceCell)
            }
            is Category.Transport -> {
                val balance = sheetsApi.readCell(TRANSPORT_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(TRANSPORT_CELL, balanceCell)
            }
            is Category.Sweet -> {
                val balance = sheetsApi.readCell(SWEET_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(SWEET_CELL, balanceCell)
            }
            is Category.Cafe -> {
                val balance = sheetsApi.readCell(CAFE_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(CAFE_CELL, balanceCell)
            }
            is Category.Household -> {
                val balance = sheetsApi.readCell(HOUSEHOLD_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(HOUSEHOLD_CELL, balanceCell)
            }
            is Category.Clothes -> {
                val balance = sheetsApi.readCell(CLOTHES_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(CLOTHES_CELL, balanceCell)
            }
            is Category.Child -> {
                val balance = sheetsApi.readCell(CHILD_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(CHILD_CELL, balanceCell)
            }
            is Category.Music -> {
                val balance = sheetsApi.readCell(MUSIC_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(MUSIC_CELL, balanceCell)
            }
            is Category.Gift -> {
                val balance = sheetsApi.readCell(GIFT_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(GIFT_CELL, balanceCell)
            }
            is Category.Fun -> {
                val balance = sheetsApi.readCell(FUN_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(FUN_CELL, balanceCell)
            }
            is Category.Unexpected -> {
                val balance = sheetsApi.readCell(UNEXPECTED_CELL).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(UNEXPECTED_CELL, balanceCell)
            }
            is Unknown -> {
                val balance = sheetsApi.readCell(UNKNOWN_SELLER).toDouble()
                balanceCell = balance - spent

                sheetsApi.updateCell(UNKNOWN_SELLER, balanceCell)
            }
        }

        if (isFixResolve) {
            val unknown = sheetsApi.readCell(UNKNOWN_SELLER).toDouble()
            sheetsApi.updateCell(UNKNOWN_SELLER, unknown + spent)
        }

        return balanceCell
    }

    private fun resolveSmsExchange(smsExchange: SmsData.SmsExchange, sheetsApi: SheetsApi, timeMillis: Long) {
        toastUI(app, "Перевод USD $ ${smsExchange.exchangedUSD}")

        when (smsExchange.sender) {
            is PriorBank -> {
                val changedUsd = sheetsApi.readCell(CHANGED_USD_CELL).toDouble()
                val toSpendUsd = sheetsApi.readCell(TO_SPEND_USD_CELL).toDouble()
                val onCardUsd = sheetsApi.readCell(ON_THE_CARD_USD_CELL).toDouble()

                sheetsApi.updateCell(PRIOR_BALANCE_CELL, smsExchange.actualBalance)
                sheetsApi.updateCell(CHANGED_USD_CELL, changedUsd + smsExchange.exchangedUSD)
                sheetsApi.updateCell(TO_SPEND_USD_CELL, toSpendUsd - smsExchange.exchangedUSD)
                sheetsApi.updateCell(ON_THE_CARD_USD_CELL, onCardUsd - smsExchange.exchangedUSD)

                runBlocking {
                    app.datastore.edit {
                        if (smsExchange.sender is PriorBank) {
                            it[app.priorBalance] = smsExchange.actualBalance.toLong()
                        } else {
                            it[app.mtbankBalance] = smsExchange.actualBalance.toLong()
                        }
                    }
                }

                App.db.logDao()
                    .insert(
                        LogEntity(
                            sender = smsExchange.sender.name, seller = "Перевод",
                            actualBalance = smsExchange.actualBalance, spent = 0.0,
                            categoryBalance = 0.0, sellerText = "Перевод USD $ ${smsExchange.exchangedUSD}",
                            timeInMillis = timeMillis, isSellerResolved = true
                        )
                    )
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