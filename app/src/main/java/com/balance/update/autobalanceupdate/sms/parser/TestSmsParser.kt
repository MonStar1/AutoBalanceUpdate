package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.category.Category

class TestSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        return SmsData.SmsSpent(SmsSender.Mtbank(), Category.Food, 1.1, 50.0, "Unknown")
    }
}