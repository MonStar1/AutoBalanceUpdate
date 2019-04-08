package com.balance.update.autobalanceupdate.domain.currency

import com.balance.update.autobalanceupdate.data.services.sms.parser.Currency
import com.balance.update.autobalanceupdate.domain.SingleInteractor
import io.reactivex.Single

data class CurrencyInput(val inputCurrency: Currency, val outputCurrency: Currency, val inputValue: Double)

class ConvertCurrency : SingleInteractor<Double, CurrencyInput>() {

    override fun buildCase(params: CurrencyInput): Single<Double> {
        return Single.create<Double> {
            val result: Double = when (params.inputCurrency) {
                Currency.USD -> {
                    when (params.outputCurrency) {
                        Currency.BYN -> params.inputValue * 2.13
                        Currency.EUR -> params.inputValue * 0.89
                        else -> params.inputValue
                    }
                }
                Currency.EUR -> {
                    when (params.outputCurrency) {
                        Currency.BYN -> params.inputValue * 2.4
                        Currency.USD -> params.inputValue * 1.118
                        else -> params.inputValue
                    }
                }
                Currency.BYN -> {
                    when (params.outputCurrency) {
                        Currency.EUR -> params.inputValue * 0.41
                        Currency.USD -> params.inputValue * 0.46
                        else -> params.inputValue
                    }
                }
            }

            it.onSuccess(result)
        }
    }
}