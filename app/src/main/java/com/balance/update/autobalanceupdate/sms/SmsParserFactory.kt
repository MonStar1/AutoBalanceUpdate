package com.balance.update.autobalanceupdate.sms

class SmsParserFactory(val from: String, val body: String) {

    private fun getSender(): SmsSender {
        return when (from) {
            SmsSender.Mtbank().name -> SmsSender.Mtbank()
            SmsSender.PriorBank().name -> SmsSender.PriorBank()
            else -> {
                throw IllegalStateException("Unknown SMS sender")
            }
        }
    }

    fun getParser(): SmsParser {
        return when (getSender()) {
            is SmsSender.Mtbank -> MtbankSmsParser(body)
            is SmsSender.PriorBank -> PriorbankSmsParser(body)
        }
    }
}