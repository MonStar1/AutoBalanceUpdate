package com.balance.update.autobalanceupdate.sms

class MtbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        val spent = getSpent()
        val balance = getActualBalance()

        return SmsData(SmsSender.Mtbank(), "unknown", spent, balance)
    }

    private fun getSpent(): Double {
        val matcher = buildPattern("OPLATA", "BYN").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            return 0.0
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