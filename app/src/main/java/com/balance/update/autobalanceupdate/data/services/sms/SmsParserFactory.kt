package com.balance.update.autobalanceupdate.data.services.sms

import com.balance.update.autobalanceupdate.data.services.sms.parser.*

class SmsParserFactory(val from: String, val body: String) {

    private fun getSender(): SmsSender {
        return SmsSender.values().find { it.value == from }
                ?: throw SmsSenderException("Unknown SMS sender")
    }

    fun getParser(): SmsParser {
        return when (getSender()) {
            SmsSender.Mtbank -> MtbankSmsParser(body)
            SmsSender.PriorBank -> PriorbankSmsParser(body)
            SmsSender.Test -> TestSmsParser(body)
        }
    }
}