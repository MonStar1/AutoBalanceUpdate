package com.balance.update.autobalanceupdate.sms

interface SmsParser {
    fun parse(): SmsData
}

data class SmsData(val sender: SmsSender, val seller: String, val spent: Double, val actualBalance: Double)


class SmsParseException : Exception("Incorrect type of sms")