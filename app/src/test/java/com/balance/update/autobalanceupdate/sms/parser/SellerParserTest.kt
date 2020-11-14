package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.category.Category
import org.junit.Assert.*
import org.junit.Test

class SellerParserTest {

    private val sellerParser = CategoryParser("OPLATA", "BYN");

    @Test
    fun testUnknown() {
        val seller = sellerParser.getCategory("WTF BYN")

        assertTrue(seller.first is Category.Unknown)
    }

    @Test
    fun testSosediSeller() {
        var body = """KARTA:5351*1635
18/08/18 13:56
OPLATA 15.86 BYN
PT CT "KORONA", , MINSK
OSTATOK 123.39 BYN
Spr.:5099999"""

        val seller = sellerParser.getCategory(body)

        assertTrue(seller.first is Category.Food)
    }

    @Test
    fun testAptekaSeller() {
        var body = """KARTA 5*1635
2019-07-25 11:31:30
OPLATA 2.26BYN
PT SHOP APTEKA N1 GRODNO BY
OSTATOK 2.03BYN
Spr.:5099999"""

        val seller = sellerParser.getCategory(body)

        assertTrue(seller.first is Category.Health)
    }

    @Test
    fun testAzsSeller() {
        var body = """KARTA 5*1635
2019-07-25 11:31:30
OPLATA 2.26BYN
SUP AZS N1 GRODNO BY
OSTATOK 2.03BYN
Spr.:5099999"""

        val seller = sellerParser.getCategory(body)

        assertTrue(seller.first is Category.Transport)
    }

    @Test
    fun testCafeSeller() {
        var body = """KARTA 5*1635
2020-03-21 14:22:22
OPLATA 28.00 BYN
SHOP"WWW.GOGOPIZZA.BY" / GRODNO / BY
OSTATOK 225.78 BYN
Spr.:5099999"""

        val seller = sellerParser.getCategory(body)

        assertTrue(seller.first is Category.Cafe)
    }
}