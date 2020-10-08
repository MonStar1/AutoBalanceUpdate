package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.seller.Seller
import java.util.regex.Pattern

class SellerParser(private val patternPrefix: String, private val patterPostfix: String) {

    fun getSeller(body: String): Pair<Seller, String> {
        val food = buildCommonPatternPay(Seller.Food.shopsArray).matcher(body)
        val health = buildCommonPatternPay(Seller.Health.shopsArray).matcher(body)
        val transport = buildCommonPatternPay(Seller.Transport.shopsArray).matcher(body)
        val sweet = buildCommonPatternPay(Seller.Sweet.shopsArray).matcher(body)
        val cafe = buildCommonPatternPay(Seller.Cafe.shopsArray).matcher(body)
        val household = buildCommonPatternPay(Seller.Household.shopsArray).matcher(body)
        val music = buildCommonPatternPay(Seller.Music.shopsArray).matcher(body)
        val clothes = buildCommonPatternPay(Seller.Clothes.shopsArray).matcher(body)
        val child = buildCommonPatternPay(Seller.Child.shopsArray).matcher(body)

        return when {
            food.find() -> Pair(Seller.Food, food.group(2))
            health.find() -> Pair(Seller.Health, health.group(2))
            transport.find() -> Pair(Seller.Transport, transport.group(2))
            sweet.find() -> Pair(Seller.Sweet, sweet.group(2))
            cafe.find() -> Pair(Seller.Cafe, cafe.group(2))
            household.find() -> Pair(Seller.Household, household.group(2))
            clothes.find() -> Pair(Seller.Clothes, clothes.group(2))
            child.find() -> Pair(Seller.Child, child.group(2))
            music.find() -> Pair(Seller.Music, music.group(2))
            else -> Pair(Seller.Unknown("Unknown"), "Unknown")
        }
    }

    private fun buildCommonPatternPay(shopArray: Array<String>): Pattern {
        val str = buildString {
            append("(")
            for ((i, shop) in shopArray.withIndex()) {
                append(shop)

                if (i != shopArray.size - 1) {
                    append("|")
                }
            }
            append(")")
        }

        return SmsParser.buildPattern(patternPrefix, "$patterPostfix\\s.*$str")
    }
}