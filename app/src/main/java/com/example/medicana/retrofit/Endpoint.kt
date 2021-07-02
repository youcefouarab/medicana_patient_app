package com.example.medicana.retrofit

import com.example.medicana.entity.*
import retrofit2.Call
import retrofit2.http.*

interface Endpoint {

    @GET("auth_patient/{phone_number}/{password}")
    fun authPatient(
        @Path("phone_number") phone_number: String?,
        @Path("password") password: String?
    ): Call<Patient>

    @GET("patient_doctors/{patient_id}")
    fun getMyDoctors(
        @Path("patient_id") patient_id: Long?
    ): Call<List<Doctor>>

    @GET("patient_appointments/{patient_id}")
    fun getMyAppointments(
        @Path("patient_id") patient_id: Long?
    ): Call<List<Appointment>>

    @GET("all_patient_advice/{patient_id}")
    fun getAllAdvice(
        @Path("patient_id") patient_id: Long?
    ): Call<List<Advice>>

    @GET("doctors")
    fun getDoctors(): Call<List<Doctor>>

    @GET("availabilities/{doctor_id}/{date}/{time}")
    fun getAvailabilitiesForDoctorByDate(
        @Path("doctor_id") doctor_id: Long?,
        @Path("date") date: String?,
        @Path("time") time: String?
    ): Call<List<Appointment>>

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

    @POST("register/{user_type}/{user_id}/{token}")
    fun registerToken(
        @Path("user_type") user_type: String? = "patient",
        @Path("user_id") user_id: Long?,
        @Path("token") token: String?
    ): Call<Long>

    @DELETE("unregister/{device_id}")
    fun unregisterToken(
        @Path("device_id") device_id: Long?
    ): Call<String>

    @GET("patient_treatments/{patient_id}")
    fun getMyTreatments(
            @Path("patient_id") patient_id: Long?
    ): Call<List<Treatment>>

}

