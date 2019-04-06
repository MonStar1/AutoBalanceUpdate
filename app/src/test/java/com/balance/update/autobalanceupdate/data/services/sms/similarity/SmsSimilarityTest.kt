package com.balance.update.autobalanceupdate.data.services.sms.similarity

import net.ricecode.similarity.JaroWinklerStrategy
import net.ricecode.similarity.StringSimilarityServiceImpl
import org.junit.Assert
import org.junit.Test

class SmsSimilarityTest {

    val smsALMI =
            """
KARTA:5351*1635
03/04/19 20:23
OPLATA 34.96 BYN
UNIVERSAM "ALMI", , GRODNO
OSTATOK 27.85 BYN
Spr.:5099999
            """


    val smsALMI_2 =
            """
UNIVERSAM "ALMI", , GRODNO
            """

    val smsEuroopt =
            """
KARTA:5351*1635
06/04/19 14:16
OPLATA 5.72 BYN
SHOP "EVROOPT" MVV, , GRODNO
OSTATOK 97.36 BYN
Spr.:5099999
            """

    val smsEuroopt_2 =
            """
SHOP "EVROOPT", , MINSK
            """

    val similarService = StringSimilarityServiceImpl(JaroWinklerStrategy())

    @Test
    fun testSmsAlmi() {
        val score = similarService.score(smsALMI, smsALMI_2)

        Assert.assertTrue(score.toString(), score > 0.9F)
    }

    @Test
    fun testSmsEuroopt() {
        val score = similarService.score(smsEuroopt, smsEuroopt_2)

        Assert.assertTrue(score.toString(), score > 0.9F)
    }

    @Test
    fun testSmsEurooptAndAlmi() {
        val score = similarService.score(smsEuroopt, smsALMI)

        Assert.assertTrue(score.toString(), score < 0.9F)
    }
}