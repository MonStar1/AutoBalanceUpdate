package com.balance.update.autobalanceupdate.sms

import android.app.IntentService
import android.content.Intent
import android.provider.Telephony
import android.text.format.DateFormat
import android.widget.Toast
import com.balance.update.autobalanceupdate.App
import com.balance.update.autobalanceupdate.extension.toastUI
import com.balance.update.autobalanceupdate.sms.parser.SmsData
import com.balance.update.autobalanceupdate.sms.parser.SmsParseException
import com.balance.update.autobalanceupdate.sms.parser.SmsSenderException
import java.util.Date

class SmsParserService : IntentService("SmsService") {

    override fun onHandleIntent(intent: Intent) {
        Telephony.Sms.Intents.getMessagesFromIntent(intent).forEach {
            try {
                handleMessage(it.originatingAddress, it.messageBody, it.timestampMillis)
            } catch (ex: Throwable) {
                Toast.makeText(this, "Some excepion: ${ex.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleMessage(sender: String, messageBody: String, timeMillis: Long) {
        val date = DateFormat.getDateFormat(this).format(Date(timeMillis))
        toastUI(this, "message: $messageBody, date: $date")

        val smsData: SmsData

        try {
            smsData = SmsParserFactory(sender, messageBody).getParser().parse()
        } catch (ex: SmsParseException) {
            toastUI(this, "Unknown sms type: $messageBody")
            return
        } catch (ex: SmsSenderException) {
            toastUI(this, "Unknown sms sender: $sender")
            return
        }

        SmsResolver(application as App).resolve(smsData, timeMillis)
    }
}