package com.balance.update.autobalanceupdate.data.services.sms

import com.balance.update.autobalanceupdate.data.services.sms.parser.*

class SmsParserFactory(val from: String, val body: String) {

    private fun getSender(): SmsSender {
        return when (from) {
            SmsSender.Mtbank().name -> SmsSender.Mtbank()
            SmsSender.PriorBank().name -> SmsSender.PriorBank()
            SmsSender.Test().name -> SmsSender.Test()
            else -> {
                throw SmsSenderException("Unknown SMS sender")
            }
        }
    }

    fun getParser(): SmsParser {
        return when (getSender()) {
            is SmsSender.Mtbank -> MtbankSmsParser(body)
            is SmsSender.PriorBank -> PriorbankSmsParser(body)
            is SmsSender.Test -> TestSmsParser(body)
        }
    }
}