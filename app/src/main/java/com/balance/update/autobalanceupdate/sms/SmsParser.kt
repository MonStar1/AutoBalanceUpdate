package com.balance.update.autobalanceupdate.sms

import java.util.regex.Pattern

interface SmsParser {
    companion object {
        private const val PATTERN_OSTATOK = ".*%s\\s*?(\\d+.?[\\d]*)\\s*?%s.*"
    }

    fun parse(): SmsData

    fun buildPattern(prefix: String, postfix: String): Pattern {
        return Pattern.compile(PATTERN_OSTATOK.format(prefix, postfix), Pattern.CASE_INSENSITIVE)
    }
}

data class SmsData(val sender: SmsSender, val seller: String, val spent: Double, val actualBalance: Double)


class SmsParseException(message: String = "Incorrect type of sms") : Exception(message)