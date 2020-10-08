package com.balance.update.autobalanceupdate.sms.seller

sealed class Seller(val name: String, val shopsArray: Array<String>) {
    class Unknown(val seller: String) : Seller(seller, emptyArray())
    object Food : Seller(
        "Food", arrayOf(
            "SOSEDI",
            "KORONA",
            "EVROOPT",
            "UNIVERSAM",
            "BELMARKET",
            "ALMI",
            "VIT",
            "VESTA",
            "INDURSK",
            "SOLNESHNY",
            "GREEN",
            "GRODNENSKIY TC",
            "SANTA",
            "SUVOROVSKIY",
            "SPADAR",
            "E-DOSTA",
            "BREST"
        )
    )

    object Health : Seller(
        "Health", arrayOf(
            "APTEKA",
            "SYNEVO"
        )
    )

    object Sweet : Seller(
        "Sweet", arrayOf(
            "KRASNYY",
            "KIOSK"
        )
    )

    object Transport : Seller(
        "Transport", arrayOf(
            "AZS",
            "Taxi"
        )
    )

    object Cafe : Seller(
        "Cafe", arrayOf(
            "GOGOPIZZA",
            "KFC",
            "KAFE",
            "TRDLO",
            "GOU-GOU",
            "DODO", "PIZZA",
            "BARASHKA",
            "GO CAFE",
            "PROSTO KOFE",
            "MENU.BY"
        )
    )

    object Household : Seller("Household", arrayOf("MILA"))
    object Music : Seller("Music", arrayOf("MUSIC"))
    object Clothes : Seller(
        "Clothes", arrayOf(
            "COLINS",
            "GLORIA",
            "BELWEST"
        )
    )

    object Child : Seller(
        "Child", arrayOf(
            "BUSLIK",
            "BEBIMARK",
            "PESOCHNICA",
            "PODGUZNIK"
        )
    )
}