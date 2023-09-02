package com.cokchi.sogating_final.message.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cokchi.sogating_final.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//유저의 토큰정보를 받아와서 파이어베이스서버로 메세지 보냄
//파이어베이스 서버에서 앱으로 메세지 보내고 앱에서는 알람띄우도록 구현
class FireBaseService : FirebaseMessagingService() {

    private val TAG = "FireBaseService"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.e(TAG, message.notification?.title.toString())
        Log.e(TAG, message.notification?.body.toString())

        val title = message.notification?.title.toString()
        val body = message.notification?.title.toString()

        createNotificationChannel()
        sendNotification(title, body)
    }


    //Notification기능
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TestChannel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //NOtification 기본알림기능
    private fun sendNotification(title : String, body : String) {
        var builder = NotificationCompat.Builder(this, "TestChannel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        try {
            with(NotificationManagerCompat.from(this)){
                notify(123, builder.build())
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: " + e.message)
        }
    }


}