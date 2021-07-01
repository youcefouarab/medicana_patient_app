package com.example.medicana.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class NotificationService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("firebase", "new token : $token")
    }
}