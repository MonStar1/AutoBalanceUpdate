package com.balance.update.autobalanceupdate.presentation.widget

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.balance.update.autobalanceupdate.R
import com.balance.update.autobalanceupdate.presentation.unresolved.UnresolvedSmsActivity

private const val UNRESOLVED_SMS_CHANNEL_ID = "unresolved_channel_id"

class UnresolvedSmsNotification(private val context: Context) {

    fun show(countOfUnresolvedSms: Int) {
        if (countOfUnresolvedSms <= 0) {
            hide()
            return
        }

        val notification = NotificationCompat.Builder(context, UNRESOLVED_SMS_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setVibrate(LongArray(0))
                .setContentTitle(context.getString(R.string.new_unresolved_sms_title))
                .setContentText(context.getString(R.string.new_unresolved_sms_content, countOfUnresolvedSms))
                .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, UnresolvedSmsActivity::class.java), PendingIntent.FLAG_CANCEL_CURRENT))
                .build()

        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(UNRESOLVED_SMS_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(false)
                setSound(null, null)
            }
            // Register the channel with the system

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notification)
    }

    fun hide() {
        NotificationManagerCompat.from(context).cancel(1)
    }
}