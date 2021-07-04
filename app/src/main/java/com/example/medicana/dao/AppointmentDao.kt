package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.MyAppointment

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointment LEFT JOIN doctor ON appointment.doctor_id = doctor.doctor_id WHERE date > :date OR (date = :date AND finish_time >= :time)")
    fun getMyCurrentAppointments(date: String, time: String): List<MyAppointment>

    @Query("SELECT * FROM appointment LEFT JOIN doctor ON appointment.doctor_id = doctor.doctor_id WHERE date < :date OR (date = :date AND finish_time < :time)")
    fun getMyOldAppointments(date: String, time: String): List<MyAppointment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAppointment(appointment: Appointment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAppointments(appointments: List<Appointment>)

    @Query("DELETE FROM appointment WHERE appointment_id = :appointment_id")
    fun deleteMyAppointment(appointment_id: Long?)

    @Query("SELECT count(*) FROM appointment WHERE doctor_id = :doctor_id")
    fun checkAppointmentsWithDoctorExist(doctor_id: Long?): Int

    @Query("DELETE FROM appointment")
    fun deleteAll()

}
