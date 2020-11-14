package com.balance.update.autobalanceupdate.sms

import com.balance.update.autobalanceupdate.sms.parser.PriorbankSmsParser
import com.balance.update.autobalanceupdate.sms.parser.SmsData
import com.balance.update.autobalanceupdate.sms.parser.SmsParseException
import com.balance.update.autobalanceupdate.sms.category.Category
import org.junit.Test

import org.junit.Assert.*

class PriorbankSmsParserTest {

    lateinit var parser: PriorbankSmsParser

    @Test
    fun parse_1() {
        parser = PriorbankSmsParser("Dostupno:100.00BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(100.0, result.actualBalance, 0.0)
    }

    @Test
    fun parse_2() {
        parser = PriorbankSmsParser("Dostupno: 100.99BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(100.99, result.actualBalance, 0.0)
    }

    @Test
    fun parse_3() {
        parser = PriorbankSmsParser("Dostupno: 100.01 BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(100.01, result.actualBalance, 0.0)
    }

    @Test
    fun parse_4() {
        parser = PriorbankSmsParser("Dostupno:100.1 BYN")

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(100.1, result.actualBalance, 0.0)
    }

    @Test
    fun parse_USD_change() {
        val body =
            "Priorbank. Karta 4***7405 26-01-2020 12:49:35. Zachislenie perevoda 100.00 USD. BLR ANDREI PAPKO. Dostupno: 292.20 BYN. Spravka: 80172899292"
        parser = PriorbankSmsParser(body)

        val result = parser.parse() as SmsData.SmsExchange

        assertEquals(292.20, result.actualBalance, 0.0)
        assertEquals(100.00, result.exchangedUSD, 0.0)
        assertEquals("Priorbank", result.sender.name)
    }

    @Test
    fun parse_Get_cash() {
        val body =
            "Priorbank. Karta 4***7405 02-02-2020 18:39:46. Nalichnye v bankomate 75.00 BYN. BLR ATM 675. Dostupno: 167.99 BYN. Spravka: 80172899292"
        parser = PriorbankSmsParser(body)

        val result = parser.parse() as SmsData.SmsGetCash

        assertEquals(167.99, result.actualBalance, 0.0)
        assertEquals(75.00, result.cashBYN, 0.0)
    }

    @Test(expected = SmsParseException::class)
    fun parse_5() {
        parser = PriorbankSmsParser("Other text")

        val result = parser.parse()
    }

    @Test
    fun parse_spent() {
        val body =
            "Priorbank. Karta 4***7405 21-08-2020 19:42:50. Oplata 140.94 BYN. BLR WWW.HIT.E-DOSTAVKA.BY. Dostupno: 233.77 BYN. Spravka: 80172899292"
        parser = PriorbankSmsParser(body)

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(233.77, result.actualBalance, 0.0)
        assertEquals(140.94, result.spent, 0.0)
        assertEquals(Category.Food, result.category)
    }

    @Test
    fun parse_spent2() {
        val body =
            "Priorbank. Karta 4***7405 27-09-2020 11:14:13. Oplata 6.84 BYN. BLR OPS GRODNO-20. Dostupno: 117.66 BYN. Spravka: 80172899292"
        parser = PriorbankSmsParser(body)

        val result = parser.parse() as SmsData.SmsSpent

        assertEquals(117.66, result.actualBalance, 0.0)
        assertEquals(6.84, result.spent, 0.0)
        assertTrue(result.category is Category.Unknown)
        assertEquals(". BLR OPS GRODNO-20.", result.sellerName)
    }
}