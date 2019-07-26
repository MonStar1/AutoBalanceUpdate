package com.balance.update.autobalanceupdate.sms.seller

sealed class Seller(private val name: String) {
    object Unknown : Seller("Unknown")
    object Food : Seller("Food")
    object Health : Seller("Health")
    object Transport : Seller("Transport")

    override fun toString(): String {
        return name
    }
}