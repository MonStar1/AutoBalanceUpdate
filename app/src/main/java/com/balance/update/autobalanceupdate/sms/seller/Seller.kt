package com.balance.update.autobalanceupdate.sms.seller

sealed class Seller(val name: String, val shopsArray: Array<String>) {
    object Unknown : Seller("Unknown", emptyArray())
    object Food : Seller("Food", arrayOf(
            "SOSEDI",
            "KORONA",
            "EVROOPT",
            "UNIVERSAM",
            "BELMARKET",
            "ALMI",
            "VITALUR",
            "VESTA",
            "INDURSK",
            "SOLNESHNY",
            "GREEN",
            "SANTA",
            "SUVOROVSKIY",
            "SPADAR",
            "E-DOSTA",
            "BREST"))

    object Health : Seller("Health", arrayOf("APTEKA", "SYNEVO"))
    object Sweet : Seller("Sweet", arrayOf("KRASNYY PISCHEVIK", "KIOSK"))
    object Transport : Seller("Transport", arrayOf("AZS", "Taxi"))
    object Cafe : Seller("Cafe", arrayOf("GOGOPIZZA",
            "KFC",
            "TRDLO",
            "GOU-GOU",
            "DODO", "PIZZA", "BARASHKA", "GO CAFE", "PROSTO KOFE", "MENU.BY"))

    object Household : Seller("Household", arrayOf("MILA"))
    object Clothes : Seller("Clothes", arrayOf(
            "COLINS",
            "GLORIA",
            "BELWEST"
    ))
    object Child : Seller("Child", arrayOf(
            "BUSLIK",
            "PODGUZNIK"
    ))
}