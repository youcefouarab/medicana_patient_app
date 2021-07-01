package com.example.medicana.entity

import java.io.Serializable

data class Patient(
    val patient_id: Long? = null,
    val phone_number: String?,
    val first_name: String?,
    val last_name: String?,
    val token: String?

): Serializable
