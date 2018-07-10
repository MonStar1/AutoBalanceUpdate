package com.balance.update.autobalanceupdate.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.balance.update.autobalanceupdate.extension.logd
import com.balance.update.autobalanceupdate.extension.toast

class SmsBroadcastReceiver : BroadcastReceiver() {

    init {
        logd("SmsBroadcastReceiver constructor")
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            val smsServiceIntent = Intent(context, SmsParserService::class.java)

            smsServiceIntent.apply {
                data = intent?.data
                action = intent?.action
                flags = intent?.flags!!

                replaceExtras(intent)
            }

            context.startService(smsServiceIntent)
        }
    }
}