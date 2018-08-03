package com.balance.update.autobalanceupdate.sms

import com.balance.update.autobalanceupdate.sms.parser.PriorbankSmsParser
import com.balance.update.autobalanceupdate.sms.parser.SmsParseException
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
}