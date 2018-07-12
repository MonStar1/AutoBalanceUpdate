package com.balance.update.autobalanceupdate.sms

class PriorbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData(SmsSender.PriorBank(), "unknown", 0.0, getActualBalance())
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("Dostupno:", "BYN").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            throw SmsParseException("Unknown Priorbank sms type")
        }
    }
}