package com.example.medicana

import androidx.multidex.MultiDexApplication
import com.example.medicana.room.RoomService

class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RoomService.context = applicationContext
    }


}