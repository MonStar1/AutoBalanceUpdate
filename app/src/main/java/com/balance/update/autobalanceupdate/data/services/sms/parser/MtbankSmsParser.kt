package com.balance.update.autobalanceupdate.data.services.sms.parser

import com.balance.update.autobalanceupdate.data.services.sms.SmsSender
import com.balance.update.autobalanceupdate.data.services.sms.seller.Seller
import java.util.regex.Pattern

class MtbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        val spent = getSpent()
        val balance = getActualBalance()
        val seller = MtbankSellerParser.getSeller(body)

        return SmsData(SmsSender.Mtbank(), spent, balance)
    }

    private fun getSpent(): Amount {
        val matcher = buildPattern("OPLATA", Currency.getPattern()).matcher(body)

        if (matcher.matches()) {
            val currency = Currency.values().firstOrNull { it.value == matcher.group(2) }

            return Amount(currency ?: Currency.BYN, matcher.group(1).toDouble())
        } else {
            return Amount(Currency.BYN, 0.0)
        }
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("OSTATOK", Currency.BYN.value).matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            throw SmsParseException("Unknown MTBank sms type")
        }
    }
}

class MtbankSellerParser {


    companion object {
        val FOOD_ARRAY = arrayOf("SHOP\\s\"SOSEDI\"", "SHOP\\s\"KORONA\"", "SHOP\\s\"EVROOPT\"", "UNIVERSAM\"ZLATOGORSKIY", "SHOP\\sN\\s130\\s\"BELMARKET\"", "UNIVERSAM\\s\"ALMI\"", "PT\\sSHOP\\sVESTA")

        fun getSeller(body: String): Seller {
            val food = buildFoodPattern()

            if (food.matcher(body).find()) {
                return Seller.Food()
            } else {
                return Seller.Unknown()
            }
        }

        private fun buildFoodPattern(): Pattern {
            val str = buildString {
                append("(")
                for ((i, food) in FOOD_ARRAY.withIndex()) {
                    append(food)

                    if (i != FOOD_ARRAY.size - 1) {
                        append("|")
                    }
                }
                append(")")
            }

            return SmsParser.buildPattern("OPLATA", "${Currency.getPattern()}\\s$str")
        }
    }
}