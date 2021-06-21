package com.example.medicana.retrofit

import com.example.medicana.entity.*
import retrofit2.Call
import retrofit2.http.*

interface Endpoint {
    @GET("doctors")
    fun getDoctors(): Call<List<Doctor>>

    @GET("patient_appointments/{patient_id}")
    fun getMyAppointments(
        @Path("patient_id") patient_id: Long?
    ): Call<List<Appointment>>

    @GET("patient_doctors/{patient_id}")
    fun getMyDoctors(
        @Path("patient_id") patient_id: Long?
    ): Call<List<Doctor>>

    @GET("availabilities/{doctor_id}/{date}/{time}")
    fun getAvailabilitiesForDoctorByDate(
        @Path("doctor_id") doctor_id: Long?,
        @Path("date") date: String?,
        @Path("time") time: String?
    ): Call<List<Appointment>>

    @GET("auth_patient/{phone_number}/{password}")
    fun authPatient(
        @Path("phone_number") phone_number: String?,
        @Path("password") password: String?
    ): Call<Patient>

    @PUT("book_appointment/{appointment_id}/{patient_id}")
    fun bookAppointment(
        @Path("appointment_id") appointment_id: Long?,
        @Path("patient_id") patient_id: Long?
    ): Call<String>

    @PUT("cancel_appointment/{appointment_id}")
    fun cancelAppointment(
        @Path("appointment_id") appointment_id: Long?
    ): Call<String>

    @POST("ask_advice")
    fun askForAdvice(@Body advice_list: List<Advice>?): Call<String>

    @PUT("see_advice/{patient_id}/{doctor_id}")
    fun seeAdvice(
        @Path("patient_id") patient_id: Long?,
        @Path("doctor_id") doctor_id: Long?
    ): Call<String>

    @GET("all_patient_advice/{patient_id}")
    fun getAllAdvice(
            @Path("patient_id") patient_id: Long?
    ): Call<List<Advice>>
}

