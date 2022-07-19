package de.selfmade4u.glowingp2p

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder


class NearbyDiscoverService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel = NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        val notification: Notification = Notification.Builder(this, channel.id)
            .setContentTitle("New mail from ")
            .setContentText("subject")
            .build()

        startForeground(1, notification)

        NearbyHelper().startAdvertising(this);2

        AppDatabase.getInstance(this).userDao().getAll();

        return START_STICKY;
    }
}