package com.balance.update.autobalanceupdate.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import com.balance.update.autobalanceupdate.extension.logd
import com.balance.update.autobalanceupdate.service.UpdateBalanceService
import com.balance.update.autobalanceupdate.sms.model.ReceivedSmsModel
import com.firebase.jobdispatcher.Constraint
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver

class SmsBroadcastReceiver : BroadcastReceiver() {

    init {
        logd("SmsBroadcastReceiver constructor")
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))

            Telephony.Sms.Intents.getMessagesFromIntent(intent).forEach {
                val extras = Bundle()

                val sender = it.originatingAddress
                val tag = "SMS $sender ${it.timestampMillis}"

                extras.putString(ReceivedSmsModel.EXTRA_SENDER, sender)
                extras.putString(ReceivedSmsModel.EXTRA_MESSAGE, it.messageBody)

                val job = dispatcher.newJobBuilder()
                        .setService(UpdateBalanceService::class.java)
                        .setTag(tag)
                        .setExtras(extras)
                        .setConstraints(Constraint.ON_ANY_NETWORK)
                        .build()

                dispatcher.mustSchedule(job)
            }
        }
    }
}