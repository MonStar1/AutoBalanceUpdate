package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.category.Category
import java.util.regex.Pattern

class MtbankSmsParser(private val body: String) : SmsParser {

    private val parser = CategoryParser("OPLATA", "BYN")

    override fun parse(): SmsData {
        val spent = getSpent()
        val balance = getActualBalance()
        var category = parser.getCategory(body).first

        val flags = Pattern.CASE_INSENSITIVE or Pattern.DOTALL
        val matcher = Pattern.compile(".*BYN\\s*?(.+?)\\s*?OSTATOK.*", flags).matcher(body)
        val sellerName: String
        if (matcher.matches()) {
            sellerName = matcher.group(1).removePrefix("\n")
            if (category is Category.Unknown) {
                category = Category.Unknown(sellerName)
            }
        } else {
            sellerName = "Unknown"
            category = Category.Unknown(sellerName)
        }


        return SmsData.SmsSpent(SmsSender.Mtbank(), category, spent, balance, sellerName)
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

