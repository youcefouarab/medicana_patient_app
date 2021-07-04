package com.example.medicana.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicana.R
import com.example.medicana.entity.Appointment
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm


class TimeAdapter(val context: Context, val data: List<Appointment>): RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_time, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.availableTime.text = data[position].start_time.toString()
        holder.itemView.setOnClickListener {
            vm.appointment = data[position]
            navController(context as Activity).navigate(R.id.action_doctorFragment_to_newAppointmentFragment)
        }
    }


    class TimeViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val availableTime = view.findViewById(R.id.available_time) as TextView
    }
}
