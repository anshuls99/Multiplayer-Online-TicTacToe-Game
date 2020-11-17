package com.example.tictactoe

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class Notification {

    fun notify(context: Context, message: String, number: Int) {
        val intent = Intent(context, Login::class.java)
        val builder = NotificationCompat.Builder(context)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle("New Request")
            .setContentText(message)
            .setNumber(number)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(true)


        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.notify("New Request", 0, builder.build())
    }


}