package com.balance.update.autobalanceupdate.data.services.sms

sealed class SmsSender(val name: String) {
    class PriorBank : SmsSender("Priorbank")
    class Mtbank : SmsSender("MTBANK")
    class Test : SmsSender("2861920")
}