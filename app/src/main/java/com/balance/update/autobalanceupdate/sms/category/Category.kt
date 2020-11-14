package com.balance.update.autobalanceupdate.sms.category

sealed class Category(val name: String, val shopsArray: Array<String>) {
    class Unknown(val sellerText: String) : Category(sellerText, emptyArray())
    object Food : Category(
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

    object Health : Category(
        "Здоровье", arrayOf(
            "APTEKA",
            "SYNEVO"
        )
    )

    object Sweet : Category(
        "Вкусняшки", arrayOf(
            "KRASNYY",
            "KIOSK"
        )
    )

    object Transport : Category(
        "Транспорт", arrayOf(
            "AZS",
            "Taxi"
        )
    )

    object Cafe : Category(
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

    object Household : Category("Хоз. расходы", arrayOf("MILA"))
    object Music : Category("Музыка", arrayOf("MUSIC"))
    object Clothes : Category(
        "Одежда", arrayOf(
            "COLINS",
            "GLORIA",
            "BELWEST"
        )
    )

    object Child : Category(
        "На малыша", arrayOf(
            "BUSLIK",
            "BEBIMARK",
            "PESOCHNICA",
            "PODGUZNIK"
        )
    )

    object Fun : Category(
        "Развлечения", arrayOf(

        )
    )

    object Gift : Category(
        "Подарки", arrayOf(

        )
    )

    object Unexpected : Category(
        "Непредвиденные расходы", arrayOf(

        )
    )
}