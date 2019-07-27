package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.seller.Seller
import org.junit.Assert.*
import org.junit.Test

class MtbankSellerParserTest {

    @Test
    fun testUnknown() {
        val seller = MtbankSellerParser.getSeller("WTF BYN")

        assertTrue(seller.first is Seller.Unknown)
    }

    @Test
    fun testSosediSeller() {
        var body = """KARTA:5351*1635
18/08/18 13:56
OPLATA 15.86 BYN
PT SHOP "SOSEDI", , MINSK
OSTATOK 123.39 BYN
Spr.:5099999"""

        val seller = MtbankSellerParser.getSeller(body)

        assertTrue(seller.first is Seller.Food)
    }

    @Test
    fun testAptekaSeller() {
        var body = """KARTA 5*1635
2019-07-25 11:31:30
OPLATA 2.26BYN
PT SHOP APTEKA N1 GRODNO BY
OSTATOK 2.03BYN
Spr.:5099999"""

        val seller = MtbankSellerParser.getSeller(body)

        assertTrue(seller.first is Seller.Health)
    }

    @Test
    fun testAzsSeller() {
        var body = """KARTA 5*1635
2019-07-25 11:31:30
OPLATA 2.26BYN
SUP AZS N1 GRODNO BY
OSTATOK 2.03BYN
Spr.:5099999"""

        val seller = MtbankSellerParser.getSeller(body)

        assertTrue(seller.first is Seller.Transport)
    }
}