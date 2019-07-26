package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.SmsSender
import com.balance.update.autobalanceupdate.sms.seller.Seller
import java.util.regex.Pattern

class MtbankSmsParser(val body: String) : SmsParser {

    override fun parse(): SmsData {
        val spent = getSpent()
        val balance = getActualBalance()
        val seller = MtbankSellerParser.getSeller(body)

        return SmsData(SmsSender.Mtbank(), seller, spent, balance)
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
        val FOOD_ARRAY = arrayOf("SHOP\\s\"SOSEDI\"", "SHOP\\s\"KORONA\"", "SHOP\\s\"EVROOPT\"", "UNIVERSAM", "\"BELMARKET\"", "\"ALMI\"", "SHOP\\sVESTA", "PT\\sMAGAZIN", "BREST")
        val HEALTH_ARRAY = arrayOf("APTEKA", "SYNEVO")
        val TRANSPORT_ARRAY = arrayOf("AZS", "Taxi")

        fun getSeller(body: String): Seller {
            val food = buildFoodPattern()
            val health = buildHealthPattern()
            val transport = buildTransportPattern()

            if (food.matcher(body).find()) {
                return Seller.Food
            } else if (health.matcher(body).find()) {
                return Seller.Health
            } else if (transport.matcher(body).find()) {
                return Seller.Transport
            } else {
                return Seller.Unknown
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