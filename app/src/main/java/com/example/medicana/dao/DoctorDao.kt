package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Doctor

@Dao
interface DoctorDao {

    @Query("SELECT * FROM doctor")
    fun getMyDoctors(): List<Doctor>

    @Query("SELECT * FROM doctor WHERE doctor_id = :doctor_id")
    fun getMyDoctor(doctor_id: Long?): Doctor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyDoctor(doctor: Doctor?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyDoctors(doctors: List<Doctor?>?)

    @Query("DELETE FROM doctor WHERE doctor_id = :doctor_id")
    fun deleteMyDoctor(doctor_id: Long?)

    @Query("DELETE FROM doctor")
    fun deleteAll()

}