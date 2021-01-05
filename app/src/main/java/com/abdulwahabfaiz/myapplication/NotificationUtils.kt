package com.abdulwahabfaiz.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationUtils {
    companion object {
       private var CHANNEL_ID = "1"

        fun notification(ctx: Context): Notification {
            setNotificationChannel(ctx)
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            return NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setContentTitle("Recording...")
                .setContentText("in progress")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        }

        private fun setNotificationChannel(ctx: Context) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val downloadChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Recordings",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                val notificationManager =
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(downloadChannel);

            }
        }
    }
}