package com.example.medicana.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicana.util.Converter
import com.example.medicana.dao.AdviceDao
import com.example.medicana.dao.AppointmentDao
import com.example.medicana.dao.DoctorDao
import com.example.medicana.dao.TreatmentDao
import com.example.medicana.entity.Advice
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.Doctor
import com.example.medicana.entity.Treatment

@Database(entities = arrayOf(Doctor::class, Appointment::class, Advice::class, Treatment::class), version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getDoctorDao(): DoctorDao
    abstract fun getAppointmentDao(): AppointmentDao
    abstract fun getAdviceDao(): AdviceDao
    abstract fun getTreatmentDao(): TreatmentDao
}