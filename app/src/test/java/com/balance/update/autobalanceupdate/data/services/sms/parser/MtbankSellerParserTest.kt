package com.balance.update.autobalanceupdate.data.services.sms.parser

import com.balance.update.autobalanceupdate.data.services.sms.seller.Seller
import org.junit.Assert.*
import org.junit.Test

class MtbankSellerParserTest {

    @Test
    fun testUnknown() {
        val seller = MtbankSellerParser.getSeller("WTF BYN")

        assertTrue(seller is Seller.Unknown)
    }

    @Test
    fun testSosediSeller() {
        var body = """KARTA:5351*1635
18/08/18 13:56
OPLATA 15.86 BYN
SHOP "SOSEDI", , MINSK
OSTATOK 123.39 BYN
Spr.:5099999"""

        val seller = MtbankSellerParser.getSeller(body)

        assertTrue(seller is Seller.Food)
    }
}