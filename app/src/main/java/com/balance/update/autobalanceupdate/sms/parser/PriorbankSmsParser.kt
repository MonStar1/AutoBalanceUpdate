package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.seller.Seller

class PriorbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData(SmsSender.PriorBank(), Seller.Unknown, 0.0, getActualBalance(), "Prior")
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("Dostupno:", "BYN").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            throw SmsParseException("Unknown Priorbank sms type")
        }
    }
}