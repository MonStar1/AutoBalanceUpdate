package com.balance.update.autobalanceupdate.sms.seller

sealed class Seller {
    object Unknown : Seller()
    object Food : Seller()
    object Health : Seller()
    object Transport : Seller()
}