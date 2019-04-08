package com.balance.update.autobalanceupdate.data.services.sms.parser

import com.balance.update.autobalanceupdate.data.services.sms.SmsSender
import com.balance.update.autobalanceupdate.data.services.sms.seller.Seller
import java.util.regex.Pattern

interface SmsParser {
    companion object {
        private const val PATTERN_BALANCE = ".*%s\\s*?(\\d+.?[\\d]*)\\s*?%s.*"

        fun buildPattern(prefix: String, postfix: String): Pattern {
            val flags = Pattern.CASE_INSENSITIVE or Pattern.DOTALL
            return Pattern.compile(PATTERN_BALANCE.format(prefix, postfix), flags)
        }
    }

    fun parse(): SmsData

    fun buildPattern(prefix: String, postfix: String): Pattern {
        return SmsParser.buildPattern(prefix, postfix)
    }
}

data class SmsData(val sender: SmsSender, var spent: Amount, val actualBalance: Double)


class SmsParseException(message: String = "Incorrect type of sms") : Exception(message)

class SmsSenderException(message: String = "Incorrect sms sender") : Exception(message)