package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.seller.Seller

class TestSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData(SmsSender.Mtbank(), Seller.Food(), Amount(Currency.BYN, 1.1), 50.0)
    }
}