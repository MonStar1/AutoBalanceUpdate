package com.balance.update.autobalanceupdate.sms

import com.balance.update.autobalanceupdate.sms.parser.MtbankSmsParser
import com.balance.update.autobalanceupdate.sms.parser.SmsData
import com.balance.update.autobalanceupdate.sms.parser.SmsParseException
import com.balance.update.autobalanceupdate.sms.seller.Seller
import org.junit.Test

import org.junit.Assert.*

class MtbankSmsParserTest {

    lateinit var parser: MtbankSmsParser

    @Test
    fun parse_1() {
        parser = MtbankSmsParser("OPLATA10BYN OSTATOK100.00BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(100.0, result.actualBalance, 0.0)
        assertEquals(10.0, result.spent, 0.0)
    }

    @Test
    fun parse_2() {
        parser = MtbankSmsParser("OPLATA 10.1 BYN OSTATOK 100.99BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(10.1, result.spent, 0.0)
        assertEquals(100.99, result.actualBalance, 0.0)
    }

    @Test
    fun parse_3() {
        parser = MtbankSmsParser("OSTATOK 100.01 BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(100.01, result.actualBalance, 0.0)
    }

    @Test
    fun parse_4() {
        parser = MtbankSmsParser("OSTATOK100.1 BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(100.1, result.actualBalance, 0.0)
    }

    @Test(expected = SmsParseException::class)
    fun parse_5() {
        parser = MtbankSmsParser("Other text")

        val result = parser.parse()
    }

    @Test
    fun parse_6() {
        parser = MtbankSmsParser("OSTATOK 1 BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(1.0, result.actualBalance, 0.0)
    }

    @Test
    fun parse_7() {
        parser = MtbankSmsParser(
            """KARTA:5351*1635
    16/08/18 12:46
    OPLATA 6.90 BYN
    WWW.HIT.E-DOSTAVKA.BY, , MINSK
    OSTATOK 62.37 BYN
    Spr.:5099999"""
        )

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(62.37, result.actualBalance, 6.90)
        assertEquals(Seller.Food, result.seller)
        assertEquals(
            "\n" +
                    "    WWW.HIT.E-DOSTAVKA.BY, , MINSK", result.seller.name
        )
    }

    @Test
    fun parse_8() {
        parser = MtbankSmsParser(
            """KARTA:5351*1635
    16/08/18 12:46
    OPLATA 6.90 BYN
    fukdostavka, , MINSK
    OSTATOK 62.37 BYN
    Spr.:5099999"""
        )

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(62.37, result.actualBalance, 6.90)
        assertEquals(
            """
    fukdostavka, , MINSK""", (result.seller as Seller.Unknown).sellerText
        )
    }
}