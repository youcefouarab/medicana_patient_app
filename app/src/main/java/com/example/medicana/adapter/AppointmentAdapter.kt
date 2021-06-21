package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.medicana.MainViewModel
import com.example.medicana.R
import com.example.medicana.entity.MyAppointment
import com.example.medicana.fragment.AppointmentsFragmentDirections
import com.example.medicana.util.displayDate
import com.example.medicana.util.navController


class AppointmentAdapter(val context: Context, val data: List<MyAppointment>): RecyclerView.Adapter<AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        return AppointmentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_appointment, parent, false)
        )
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.appointmentsDoctorName.text = "Dr. " + data[position].first_name + " " + data[position].last_name + " (" + data[position].specialty + ")"
        holder.appointmentsDate.text = displayDate(data[position].date!!)
        holder.appointmentsTime.text = data[position].time.toString()

        holder.itemView.setOnClickListener{
            (ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java)).myAppointment = data[position]
            val action = AppointmentsFragmentDirections.actionAppointmentsFragmentToAppointmentFragment(false)
            navController(context as Activity).navigate(action)
        }
    }
}

class AppointmentViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val appointmentsDoctorName = view.findViewById(R.id.appointments_doctor_name) as TextView
    val appointmentsDate = view.findViewById(R.id.appointments_date) as TextView
    val appointmentsTime = view.findViewById(R.id.appointments_time) as TextView
}