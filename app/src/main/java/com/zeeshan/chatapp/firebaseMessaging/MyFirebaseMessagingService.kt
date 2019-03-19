package com.zeeshan.chatapp.firebaseMessaging

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.model.User

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(messageRemote: RemoteMessage?) {
        super.onMessageReceived(messageRemote)


        showNotification(messageRemote)
    }

    private fun showNotification(messageRemote: RemoteMessage?) {
        if (messageRemote != null && messageRemote.data != null)
            Log.d("Tag", messageRemote.data?.toString())

        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(messageRemote!!.notification!!.title)
            .setContentText(messageRemote.notification!!.body)
            .setSmallIcon(com.zeeshan.chatapp.R.mipmap.ic_launcher_round)
            .setBadgeIconType(com.zeeshan.chatapp.R.mipmap.ic_launcher_round)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageRemote.notification!!.body)
            )
            .build()


        val click_action = messageRemote.notification!!.clickAction
        val senderId = messageRemote!!.data.get("fromId")
//        val notificationUser: User = FirebaseFirestore.getInstance().collection("Users").document(senderId!!).get()
        lateinit var notificationUser: User
        FirebaseFirestore.getInstance().collection("Users").document(senderId!!).get().addOnSuccessListener {
            if (it != null){
                notificationUser = it.data as User
            }
        }



        val intent = Intent(click_action).apply {
            putExtra("user",notificationUser)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        notificationBuilder.contentIntent = pendingIntent


        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder)
    }

}