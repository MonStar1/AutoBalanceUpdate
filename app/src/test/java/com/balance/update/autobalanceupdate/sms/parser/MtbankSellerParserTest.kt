package com.balance.update.autobalanceupdate.sms.parser

import com.balance.update.autobalanceupdate.sms.seller.Seller
import org.junit.Assert.*
import org.junit.Test

class MtbankSellerParserTest {

    @Test
    fun testSosedi() {
        val seller = MtbankSellerParser.getSeller("22:11 OPLATA 2.87 BYN SHOP \"SOSEDI\",,Minsk  OSTATOK 70 BYN")

        assertTrue(seller is Seller.Food)
    }

    @Test
    fun testUnknown() {
        val seller = MtbankSellerParser.getSeller("WTF BYN")

        assertTrue(seller is Seller.Unknown)
    }
}