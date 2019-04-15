package com.balance.update.autobalanceupdate.data.services.sms.parser

import com.balance.update.autobalanceupdate.data.services.sms.SmsSender

class PriorbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData(SmsSender.PriorBank, Amount(Currency.BYN, 0.0), getActualBalance())
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