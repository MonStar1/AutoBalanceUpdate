package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.seller.Seller
import java.util.regex.Pattern

interface SmsParser {
    companion object {
        private const val PATTERN_OSTATOK = ".*%s\\s*?(\\d+.?[\\d]*)\\s*?%s.*"

        fun buildPattern(prefix: String, postfix: String): Pattern {
            val flags = Pattern.CASE_INSENSITIVE or Pattern.DOTALL
            return Pattern.compile(PATTERN_OSTATOK.format(prefix, postfix), flags)
        }
    }

    fun parse(): SmsData

    fun buildPattern(prefix: String, postfix: String): Pattern {
        return SmsParser.buildPattern(prefix, postfix)
    }
}

data class SmsData(val sender: SmsSender, val seller: Seller, val spent: Double, val actualBalance: Double)


class SmsParseException(message: String = "Incorrect type of sms") : Exception(message)

class SmsSenderException(message: String = "Incorrect sms sender") : Exception(message)