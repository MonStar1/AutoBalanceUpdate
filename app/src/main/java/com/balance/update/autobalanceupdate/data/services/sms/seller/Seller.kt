package com.balance.update.autobalanceupdate.data.services.sms.seller

sealed class Seller {
    class Unknown : Seller()
    class Food : Seller()
}