package com.example.medicana.entity

import java.io.Serializable


data class MyAppointment(
    val appointment_id: Long?,
    val doctor_id: Long?,
    val phone_number: String?,
    val first_name: String?,
    val last_name: String?,
    val specialty: String?,
    val address: String?,
    val gender: String?,
    val date: String?,
    val time: String?,
    val longitude: Double?,
    val latitude: Double?,

): Serializable
