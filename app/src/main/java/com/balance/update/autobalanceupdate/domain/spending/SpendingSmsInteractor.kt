package com.balance.update.autobalanceupdate.domain.spending

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.SmsPattern
import com.balance.update.autobalanceupdate.data.db.entities.Spending
import com.balance.update.autobalanceupdate.data.repository.SpendingRepository
import com.balance.update.autobalanceupdate.data.services.sms.SmsParserFactory
import com.balance.update.autobalanceupdate.data.services.sms.parser.Amount
import com.balance.update.autobalanceupdate.data.services.sms.parser.Currency
import com.balance.update.autobalanceupdate.data.services.sms.parser.SmsData
import com.balance.update.autobalanceupdate.domain.CompletableInteractor
import com.balance.update.autobalanceupdate.domain.ObservableInteractor
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import com.balance.update.autobalanceupdate.domain.currency.ConvertCurrency
import com.balance.update.autobalanceupdate.domain.currency.CurrencyInput
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

data class SpendingInput(val sender: String, val body: String, val dateInMillis: Long, val smsPattern: SmsPattern)

class SaveSpendingSms : CompletableInteractor<SpendingInput>() {

    private val repository = SpendingRepository()

    override fun buildCase(params: SpendingInput): Completable {
        return Single.create<SmsData> {
            val smsData = SmsParserFactory(params.sender, params.body).getParser().parse()

            it.onSuccess(smsData)
        }.flatMap { smsData ->
            ConvertCurrency().attach(CurrencyInput(
                    smsData.spent.currency,
                    Currency.BYN,
                    smsData.spent.amount
            )).map {
                smsData.apply {
                    spent = Amount(Currency.BYN, it)
                }
            }
        }.flatMapCompletable {
            repository.create(
                    it.spent.amount,
                    it.spent.currency.value,
                    it.actualBalance,
                    params.dateInMillis,
                    params.smsPattern.key!!
            ).ignoreElement()
        }
    }
}

class SubscribeSpendingByFilter : ObservableInteractor<List<Spending>, Filter>() {

    private val repository = SpendingRepository()

    override fun buildCase(params: Filter): Observable<List<Spending>> {
        return repository.subscribeByFilterId(params.key!!)
    }

}