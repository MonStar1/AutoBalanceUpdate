package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.category.Category
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

sealed class SmsData {
    data class SmsSpent(
        val sender: SmsSender,
        val category: Category,
        val spent: Double,
        val actualBalance: Double,
        val sellerName: String
    ) : SmsData()

    data class SmsExchange(val sender: SmsSender, val exchangedUSD: Double, val actualBalance: Double) : SmsData()

    data class SmsGetCash(val sender: SmsSender, val cashBYN: Double, val actualBalance: Double) : SmsData()
}


class SmsParseException(message: String = "Incorrect type of sms") : Exception(message)

class SmsSenderException(message: String = "Incorrect sms sender") : Exception(message)