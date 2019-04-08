package com.balance.update.autobalanceupdate.data.services.sms.parser

import com.balance.update.autobalanceupdate.data.services.sms.SmsSender

class MtbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        val spent = getSpent()
        val balance = getActualBalance()

        return SmsData(SmsSender.Mtbank(), spent, balance)
    }

    private fun getSpent(): Amount {
        val matcher = buildPattern("OPLATA", Currency.getPattern()).matcher(body)

        if (matcher.matches()) {
            val currency = Currency.values().firstOrNull { it.value == matcher.group(2) }

            return Amount(currency ?: Currency.BYN, matcher.group(1).toDouble())
        } else {
            return Amount(Currency.BYN, 0.0)
        }
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("OSTATOK", Currency.BYN.value).matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            throw SmsParseException("Unknown MTBank sms type")
        }
    }
}