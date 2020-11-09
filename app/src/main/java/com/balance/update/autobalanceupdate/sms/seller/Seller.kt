package com.balance.update.autobalanceupdate.sms.seller

sealed class Seller(var name: String, val shopsArray: Array<String>) {
    class Unknown(val sellerText: String) : Seller(sellerText, emptyArray())
    object Food : Seller(
        "Еда", arrayOf(
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
        "Здоровье", arrayOf(
            "APTEKA",
            "SYNEVO"
        )
    )

    object Sweet : Seller(
        "Вкусняшки", arrayOf(
            "KRASNYY",
            "KIOSK"
        )
    )

    object Transport : Seller(
        "Транспорт", arrayOf(
            "AZS",
            "Taxi"
        )
    )

    object Cafe : Seller(
        "Кафе и доставка", arrayOf(
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

    object Household : Seller("Хоз. расходы", arrayOf("MILA"))
    object Music : Seller("Музыка", arrayOf("MUSIC"))
    object Clothes : Seller(
        "Одежда", arrayOf(
            "COLINS",
            "GLORIA",
            "BELWEST"
        )
    )

    object Child : Seller(
        "На малыша", arrayOf(
            "BUSLIK",
            "BEBIMARK",
            "PESOCHNICA",
            "PODGUZNIK"
        )
    )

    object Fun : Seller(
        "Развлечения", arrayOf(

        )
    )

    object Gift : Seller(
        "Подарки", arrayOf(

        )
    )

    object Unexpected : Seller(
        "Непредвиденные расходы", arrayOf(

        )
    )
}