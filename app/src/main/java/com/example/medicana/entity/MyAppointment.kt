package com.example.medicana.entity

import java.io.Serializable


data class MyAppointment(
    val appointment_id: Long?,
    val date: String?,
    val start_time: String?,
    val finish_time: String?,
    val doctor_id: Long?,
    val phone_number: String?,
    val first_name: String?,
    val last_name: String?,
    val gender: String?,
    val photo: String?,
    val specialty: String?,
    val address: String?,
    val longitude: Double?,
    val latitude: Double?,
    val token: String?

): Serializable
