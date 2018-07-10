package com.balance.update.autobalanceupdate.sms

import org.junit.Test

import org.junit.Assert.*

class MtbankSmsParserTest {

    lateinit var parser: MtbankSmsParser

    @Test
    fun parse_1() {
        parser = MtbankSmsParser("OPLATA10BYN OSTATOK100.00BYN")

        val result = parser.parse()

        assertEquals(10.0, result.spent, 0.0)
        assertEquals(100.0, result.actualBalance, 0.0)
    }

    @Test
    fun parse_2() {
        parser = MtbankSmsParser("OSTATOK 100.99BYN")

        val result = parser.parse()

        assertEquals(100.99, result.actualBalance, 0.0)
    }

    @Test
    fun parse_3() {
        parser = MtbankSmsParser("OSTATOK 100.01 BYN")

        val result = parser.parse()

        assertEquals(100.01, result.actualBalance, 0.0)
    }

    @Test
    fun parse_4() {
        parser = MtbankSmsParser("OSTATOK100.1 BYN")

        val result = parser.parse()

        assertEquals(100.1, result.actualBalance, 0.0)
    }

    @Test
    fun parse_5() {
        parser = MtbankSmsParser("Other text")

        val result = parser.parse()

        assertEquals(0.0, result.actualBalance, 0.0)
    }
}