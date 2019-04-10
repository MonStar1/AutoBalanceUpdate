package com.balance.update.autobalanceupdate.data.services.sms.parser

import com.balance.update.autobalanceupdate.data.services.sms.SmsSender

class TestSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData(SmsSender.Mtbank, Amount(Currency.BYN, 1.1), 50.0)
    }
}