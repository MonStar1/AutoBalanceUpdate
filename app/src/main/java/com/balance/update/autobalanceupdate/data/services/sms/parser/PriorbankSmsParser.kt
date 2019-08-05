package com.balance.update.autobalanceupdate.data.services.sms.parser

import com.balance.update.autobalanceupdate.data.services.sms.SmsSender

class PriorbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData(SmsSender.PriorBank, getSpent(), getActualBalance())
    }

    private fun getSpent(): Amount {
        val matcher = buildPattern("Oplata", Currency.getPattern()).matcher(body)

        if (matcher.matches()) {
            val currency = Currency.values().firstOrNull { it.value == matcher.group(2) }

            return Amount(currency ?: Currency.BYN, matcher.group(1).toDouble())
        } else {
            return Amount(Currency.BYN, 0.0)
        }
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("Dostupno:", Currency.getPattern()).matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            return 0.0
        }
    }
}