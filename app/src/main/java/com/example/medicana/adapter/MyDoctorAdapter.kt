package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.entity.Doctor
import com.example.medicana.util.navController
import com.example.medicana.room.RoomService
import com.example.medicana.service.AdviceUpdateSyncService
import com.example.medicana.util.BASE_URL
import com.example.medicana.viewmodel.VM.vm


class MyDoctorAdapter(val context: Context, val data: List<Doctor>): RecyclerView.Adapter<MyDoctorAdapter.MyDoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDoctorViewHolder {
        return MyDoctorViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_my_doctor, parent, false)
        )
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyDoctorViewHolder, position: Int) {
        holder.doctorsName.text = "Dr. " + data[position].first_name + " " + data[position].last_name
        holder.doctorsSpecialty.text = data[position].specialty
        if (data[position].photo != null) {
            Glide.with(context).load(BASE_URL + data[position].photo).into(holder.doctorsPhoto)
        } else {
            if (data[position].gender == "male") {
                Glide.with(context).load(R.drawable.default_doctor_male).into(holder.doctorsPhoto)
            } else {
                Glide.with(context).load(R.drawable.default_doctor_female).into(holder.doctorsPhoto)
            }
        }
        val unread = RoomService.appDatabase.getAdviceDao().checkUnreadFromDoctor(data[position].doctor_id)
        if (unread > 0) {
            Glide.with(context).load(R.drawable.ic_advice).into(holder.unreadIndicator)
        }
        holder.itemView.setOnClickListener{
            vm.doctor = data[position]
            navController(context as Activity).navigate(R.id.action_advicesFragment_to_adviceFragment)
        }
    }

    class MyDoctorViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val doctorsPhoto = view.findViewById(R.id.my_doctor_photo) as ImageView
        val doctorsName = view.findViewById(R.id.my_doctor_name) as TextView
        val doctorsSpecialty = view.findViewById(R.id.my_doctor_specialty) as TextView
        val unreadIndicator = view.findViewById(R.id.unread_indicator) as ImageView
    }
}

