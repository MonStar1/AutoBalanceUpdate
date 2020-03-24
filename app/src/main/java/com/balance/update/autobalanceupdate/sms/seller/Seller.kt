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
            "VESTA",
            "INDURSK",
            "SOLNESHNY",
            "GREEN",
            "SANTA",
            "BREST"))

    object Health : Seller("Health", arrayOf("APTEKA", "SYNEVO"))
    object Sweet : Seller("Sweet", arrayOf("KRASNYY PISCHEVIK", "KIOSK"))
    object Transport : Seller("Transport", arrayOf("AZS", "Taxi"))
    object Cafe : Seller("Cafe", arrayOf("GOGOPIZZA", "KFC", "Dodo Pitstsa", "BARASHKA", "GO CAFE", "PROSTO KOFE", "MENU.BY"))
    object Household : Seller("Household", arrayOf("MILA"))
    object Clothes : Seller("Clothes", arrayOf("COLINS"))
    object Child : Seller("Child", arrayOf("BUSLIK"))
}