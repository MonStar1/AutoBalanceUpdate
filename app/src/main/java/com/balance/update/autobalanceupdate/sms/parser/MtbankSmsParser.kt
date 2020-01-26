package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.seller.Seller
import java.util.regex.Pattern

class MtbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        val spent = getSpent()
        val balance = getActualBalance()
        val seller = MtbankSellerParser.getSeller(body)

        return SmsData.SmsSpent(SmsSender.Mtbank(), seller.first, spent, balance, seller.second)
    }

    private fun getSpent(): Double {
        val matcher = buildPattern("OPLATA", "BYN").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            return 0.0
        }
    }

    private fun getActualBalance(): Double {
        val matcher = buildPattern("OSTATOK", "BYN").matcher(body)

        if (matcher.matches()) {
            return matcher.group(1).toDouble()
        } else {
            throw SmsParseException("Unknown MTBank sms type")
        }
    }
}

class MtbankSellerParser {

    companion object {
        val FOOD_ARRAY = arrayOf("SOSEDI",
                "KORONA",
                "EVROOPT",
                "UNIVERSAM",
                "BELMARKET",
                "ALMI",
                "VESTA",
                "INDURSK",
                "SOLNESHNY",
                "BREST")

        val HEALTH_ARRAY = arrayOf("APTEKA", "SYNEVO")
        val TRANSPORT_ARRAY = arrayOf("AZS", "Taxi")

        fun getSeller(body: String): Pair<Seller, String> {
            val food = buildFoodPattern().matcher(body)
            val health = buildHealthPattern().matcher(body)
            val transport = buildTransportPattern().matcher(body)

            when {
                food.find() -> return Pair(Seller.Food, food.group(2))
                health.find() -> return Pair(Seller.Health, health.group(2))
                transport.find() -> return Pair(Seller.Transport, transport.group(2))
                else -> return Pair(Seller.Unknown, "Unknown")
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

            return SmsParser.buildPattern("OPLATA", "BYN\\s.*$str")
        }

        private fun buildHealthPattern(): Pattern {
            val str = buildString {
                append("(")
                for ((i, food) in HEALTH_ARRAY.withIndex()) {
                    append(food)

                    if (i != HEALTH_ARRAY.size - 1) {
                        append("|")
                    }
                }
                append(")")
            }

            return SmsParser.buildPattern("OPLATA", "BYN\\s.*$str")
        }

        private fun buildTransportPattern(): Pattern {
            val str = buildString {
                append("(")
                for ((i, food) in TRANSPORT_ARRAY.withIndex()) {
                    append(food)

                    if (i != TRANSPORT_ARRAY.size - 1) {
                        append("|")
                    }
                }
                append(")")
            }

            return SmsParser.buildPattern("OPLATA", "BYN\\s.*$str")
        }
    }
}