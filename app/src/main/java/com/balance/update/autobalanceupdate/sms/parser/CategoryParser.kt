package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.category.Category
import java.util.regex.Pattern

class CategoryParser(private val patternPrefix: String, private val patterPostfix: String) {

    fun getCategory(body: String): Pair<Category, String> {
        val food = buildCommonPatternPay(Category.Food.shopsArray).matcher(body)
        val health = buildCommonPatternPay(Category.Health.shopsArray).matcher(body)
        val transport = buildCommonPatternPay(Category.Transport.shopsArray).matcher(body)
        val sweet = buildCommonPatternPay(Category.Sweet.shopsArray).matcher(body)
        val cafe = buildCommonPatternPay(Category.Cafe.shopsArray).matcher(body)
        val household = buildCommonPatternPay(Category.Household.shopsArray).matcher(body)
        val music = buildCommonPatternPay(Category.Music.shopsArray).matcher(body)
        val clothes = buildCommonPatternPay(Category.Clothes.shopsArray).matcher(body)
        val child = buildCommonPatternPay(Category.Child.shopsArray).matcher(body)

        return when {
            food.find() -> Pair(Category.Food, food.group(2))
            health.find() -> Pair(Category.Health, health.group(2))
            transport.find() -> Pair(Category.Transport, transport.group(2))
            sweet.find() -> Pair(Category.Sweet, sweet.group(2))
            cafe.find() -> Pair(Category.Cafe, cafe.group(2))
            household.find() -> Pair(Category.Household, household.group(2))
            clothes.find() -> Pair(Category.Clothes, clothes.group(2))
            child.find() -> Pair(Category.Child, child.group(2))
            music.find() -> Pair(Category.Music, music.group(2))
            else -> Pair(Category.Unknown("Unknown"), "Unknown")
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