package com.balance.update.autobalanceupdate.sms.parser

enum class Currency(val value: String) {
    USD("USD"),
    BYN("BYN"),
    EUR("EUR");

    companion object {
        fun getPattern(): String {
            return Currency.values().joinToString("|", prefix = "(", postfix = ")")
        }
    }
}

data class Amount(val currency: Currency, val amount: Double)