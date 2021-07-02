package com.example.medicana.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.Doctor
import com.example.medicana.entity.MyAppointment
import com.example.medicana.entity.Treatment

class MainViewModel : ViewModel() {
    var doctor: Doctor? = null
    var doctors: List<Doctor>? = null
    var myAppointment: MyAppointment? = null
    var appointment: Appointment? = null
    var treatment: Treatment? = null
}