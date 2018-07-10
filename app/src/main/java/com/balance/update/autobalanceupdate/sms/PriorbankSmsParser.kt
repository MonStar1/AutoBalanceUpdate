package com.balance.update.autobalanceupdate.sms

class PriorbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData(SmsSender.PriorBank(), "unknown", 0.0, getActualBalance())
    }

    private fun getActualBalance(): Double {
        val prefix = "Dostupno:"
        val postfix = "BYN"

        if (!body.contains(prefix, ignoreCase = true) || !body.contains(postfix, ignoreCase = true)) {
            throw SmsParseException()
        }

        val lastIndexOfOstatok = body.lastIndexOf(prefix) + prefix.length
        val lastIndexOfByn = body.lastIndexOf(postfix)

        return body.substring(lastIndexOfOstatok until lastIndexOfByn).toDouble()
    }
}