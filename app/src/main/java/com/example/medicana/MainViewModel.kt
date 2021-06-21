package com.example.medicana

import androidx.lifecycle.ViewModel
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.Doctor
import com.example.medicana.entity.MyAppointment

class MainViewModel : ViewModel() {
    var doctor: Doctor? = null
    var doctors: List<Doctor>? = null
    var myAppointment: MyAppointment? = null
    var appointment: Appointment? = null
}