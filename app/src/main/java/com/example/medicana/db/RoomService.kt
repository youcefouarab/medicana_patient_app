package com.example.medicana.db

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room

@SuppressLint("StaticFieldLeak")
object RoomService {
    lateinit var context: Context
    val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "medicana").allowMainThreadQueries().build()
    }
}