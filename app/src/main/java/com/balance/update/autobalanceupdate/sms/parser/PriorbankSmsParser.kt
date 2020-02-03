package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.seller.Seller

class PriorbankSmsParser(private val body: String) : SmsParser {

    override fun parse(): SmsData {
        return if (getPerevod() != 0.0) {
            SmsData.SmsExchange(SmsSender.PriorBank(), getPerevod(), getActualBalance())
        } else if (getCash() != 0.0) {
            SmsData.SmsGetCash(SmsSender.PriorBank(), getCash(), getActualBalance())
        } else {
            SmsData.SmsSpent(SmsSender.PriorBank(), Seller.Unknown, 0.0, getActualBalance())
        }
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("Dostupno:", "BYN").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            throw SmsParseException("Unknown Priorbank sms type")
        }
    }

    private fun getPerevod(): Double {
        val matcher = buildPattern("Zachislenie perevoda", "USD").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            return 0.0
        }
    }

    private fun getCash(): Double {
        val matcher = buildPattern("Nalichnye v bankomate", "BYN").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            return 0.0
        }
    }
}