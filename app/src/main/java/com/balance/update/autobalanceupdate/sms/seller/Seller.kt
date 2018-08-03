package com.balance.update.autobalanceupdate.sms.seller

sealed class Seller {
    class Unknown : Seller()
    class Food : Seller()
}