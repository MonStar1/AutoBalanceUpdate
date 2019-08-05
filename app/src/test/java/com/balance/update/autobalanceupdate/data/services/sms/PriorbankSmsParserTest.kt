package com.balance.update.autobalanceupdate.data.services.sms

import com.balance.update.autobalanceupdate.data.services.sms.parser.Currency
import com.balance.update.autobalanceupdate.data.services.sms.parser.PriorbankSmsParser
import com.balance.update.autobalanceupdate.data.services.sms.parser.SmsParseException
import org.junit.Test

import org.junit.Assert.*

class PriorbankSmsParserTest {

    lateinit var parser: PriorbankSmsParser

    @Test
    fun parse_1() {
        parser = PriorbankSmsParser("Dostupno:100.00BYN")

        val result = parser.parse()

        assertEquals(100.0, result.actualBalance, 0.0)
    }

    @Test
    fun parse_2() {
        parser = PriorbankSmsParser("Dostupno: 100.99BYN")

        val result = parser.parse()

        assertEquals(100.99, result.actualBalance, 0.0)
    }

    @Test
    fun parse_3() {
        parser = PriorbankSmsParser("Dostupno: 100.01 BYN")

        val result = parser.parse()

        assertEquals(100.01, result.actualBalance, 0.0)
    }

    @Test
    fun parse_4() {
        parser = PriorbankSmsParser("Dostupno:100.1 BYN")

        val result = parser.parse()

        assertEquals(100.1, result.actualBalance, 0.0)
    }

    @Test(expected = SmsParseException::class)
    fun parse_5() {
        parser = PriorbankSmsParser("Other text")

        val result = parser.parse()
    }

    @Test
    fun parse_spent() {
        parser = PriorbankSmsParser("Oplata 16.55 BYN. BLR UNIVERSAM ALMI Dostupno:100.1 BYN")

        val result = parser.parse()

        assertEquals(100.1, result.actualBalance, 0.0)
        assertEquals(16.55, result.spent.amount, 0.0)
        assertEquals(Currency.BYN, result.spent.currency)
    }
}