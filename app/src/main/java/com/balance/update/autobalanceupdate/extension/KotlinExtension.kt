package com.balance.update.autobalanceupdate.extension

inline fun <T : Any> T?.ifNull(noinline ifNotNull: ((T) -> Unit)? = null, ifNull: () -> Unit) {
    if (this == null) {
        ifNull.invoke()
    } else {
        ifNotNull?.invoke(this)
    }
}

