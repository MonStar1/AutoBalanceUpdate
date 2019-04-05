package com.balance.update.autobalanceupdate.data.services.sms.parser

import com.balance.update.autobalanceupdate.data.services.sms.SmsSender
import com.balance.update.autobalanceupdate.data.services.sms.seller.Seller

class PriorbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData(SmsSender.PriorBank(), Seller.Unknown(), Amount(Currency.BYN, 0.0), getActualBalance())
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("Dostupno:", Currency.getPattern()).matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            throw SmsParseException("Unknown Priorbank sms type")
        }
    }
}