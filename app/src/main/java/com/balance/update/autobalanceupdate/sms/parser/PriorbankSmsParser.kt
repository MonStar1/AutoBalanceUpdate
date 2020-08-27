package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.seller.Seller

class PriorbankSmsParser(private val body: String) : SmsParser {

    private val parser = SellerParser("Oplata", "BYN\\S")

    override fun parse(): SmsData {
        return when {
            getPerevod() != 0.0 -> {
                SmsData.SmsExchange(SmsSender.PriorBank(), getPerevod(), getActualBalance())
            }
            getCash() != 0.0 -> {
                SmsData.SmsGetCash(SmsSender.PriorBank(), getCash(), getActualBalance())
            }
            else -> {
                val seller = parser.getSeller(body)
                val spent = getSpent()
                SmsData.SmsSpent(SmsSender.PriorBank(), seller.first, spent, getActualBalance())
            }
        }
    }

    private fun getSpent(): Double {
        val matcher = buildPattern("Oplata", "BYN").matcher(body)

        return if (matcher.matches()) {
            matcher.group(1).toDouble()
        } else {
            0.0
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

        return if (matcher.matches()) {
            matcher.group(1).toDouble()
        } else {
            0.0
        }
    }

    private fun getCash(): Double {
        val matcher = buildPattern("Nalichnye v bankomate", "BYN").matcher(body)

        return if (matcher.matches()) {
            matcher.group(1).toDouble()
        } else {
            0.0
        }
    }
}