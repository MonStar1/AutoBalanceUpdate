package com.balance.update.autobalanceupdate.sms

class MtbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        val spent = getSpent()
        val balance = getActualBalance()

        return SmsData(SmsSender.Mtbank(), "unknown", spent, balance)
    }

    private fun getSpent(): Double {
        val prefix = "OPLATA"
        val postfix = "BYN"

        val indexOfOplata = body.indexOf(prefix)

        if (!body.contains(prefix, ignoreCase = true) || !body.contains(postfix, ignoreCase = true)) {
            return 0.0
        }

        val lastIndexOfOstatok = indexOfOplata + prefix.length
        val lastIndexOfByn = body.indexOf(postfix)

        return body.substring(lastIndexOfOstatok until lastIndexOfByn).toDouble()
    }

    private fun getActualBalance(): Double {
        val prefix = "OSTATOK"
        val postfix = "BYN"

        if (!body.contains(prefix, ignoreCase = true) || !body.contains(postfix, ignoreCase = true)) {
            throw SmsParseException()
        }

        val lastIndexOfOstatok = body.lastIndexOf(prefix) + prefix.length
        val lastIndexOfByn = body.lastIndexOf(postfix)

        return body.substring(lastIndexOfOstatok until lastIndexOfByn).toDouble()
    }
}