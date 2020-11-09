package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.seller.Seller
import java.util.regex.Pattern

class MtbankSmsParser(private val body: String) : SmsParser {

    private val parser = SellerParser("OPLATA", "BYN")

    override fun parse(): SmsData {
        val spent = getSpent()
        val balance = getActualBalance()
        var seller = parser.getSeller(body).first

        val flags = Pattern.CASE_INSENSITIVE or Pattern.DOTALL
        val matcher = Pattern.compile(".*BYN\\s*?(.+?)\\s*?OSTATOK.*", flags).matcher(body)
        if (matcher.matches()) {
            val name = matcher.group(1).removePrefix("\n")
            if (seller is Seller.Unknown) {
                seller = Seller.Unknown(name)
            } else {
                seller.name = name
            }
        }


        return SmsData.SmsSpent(SmsSender.Mtbank(), seller, spent, balance)
    }

    private fun getSpent(): Double {
        val matcher = buildPattern("OPLATA", "BYN").matcher(body)

        return if (matcher.matches()) {
            matcher.group(1).toDouble()
        } else {
            0.0
        }
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("OSTATOK", "BYN").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            throw SmsParseException("Unknown MTBank sms type")
        }
    }
}

