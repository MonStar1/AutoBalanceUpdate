package com.balance.update.autobalanceupdate.sms

import com.balance.update.autobalanceupdate.sms.parser.PriorbankSmsParser
import com.balance.update.autobalanceupdate.sms.parser.SmsData
import com.balance.update.autobalanceupdate.sms.parser.SmsParseException
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
        val body = "Priorbank. Karta 4***7405 26-01-2020 12:49:35. Zachislenie perevoda 100.00 USD. BLR ANDREI PAPKO. Dostupno: 292.20 BYN. Spravka: 80172899292"
        parser = PriorbankSmsParser(body)

        val result = parser.parse() as SmsData.SmsExchange

        assertEquals(292.20, result.actualBalance, 0.0)
        assertEquals(100.00, result.exchangedUSD, 0.0)
    }

    @Test(expected = SmsParseException::class)
    fun parse_5() {
        parser = PriorbankSmsParser("Other text")

        val result = parser.parse()
    }
}