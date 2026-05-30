package com.group02.mobile.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // CỨ ĐÚNG GIỜ LÀ HỆ THỐNG ĐÁNH THỨC HÀM NÀY CHẠY!
        val title = intent.getStringExtra("title") ?: "Đến giờ học rồi! 📚"
        val body = intent.getStringExtra("body") ?: "Mau vào ôn luyện cùng Hime-sama nào!"

        banThongBaoChinhXac(context, title, body)
    }

    private fun banThongBaoChinhXac(context: Context, title: String, body: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "nihonlish_alarm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Báo thức học tập chính xác", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}