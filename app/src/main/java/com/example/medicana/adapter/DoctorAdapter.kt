package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.entity.Doctor
import com.example.medicana.util.BASE_URL
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm


class DoctorAdapter(val context: Context, val data: List<Doctor>): RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_doctor, parent, false)
        )
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.doctorsName.text = "Dr. " + data[position].first_name + " " + data[position].last_name
        holder.doctorsSpecialty.text = data[position].specialty
        holder.doctorsPhone.text = data[position].phone_number
        holder.doctorsAddress.text = data[position].address
        if (data[position].photo != null) {
            Glide.with(context).load(BASE_URL + data[position].photo).into(holder.doctorsPhoto)
        } else {
            if (data[position].gender == "male") {
                Glide.with(context).load(R.drawable.default_doctor_male).into(holder.doctorsPhoto)
            } else {
                Glide.with(context).load(R.drawable.default_doctor_female).into(holder.doctorsPhoto)
            }
        }
        holder.doctorsPhone.setOnClickListener {
            startActivity(
                context,
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + data[position].phone_number)
                ),
                null
            )
        }
        holder.doctorsDirections.setOnClickListener {
            val mapIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + data[position].longitude + "," + data[position].latitude)
            )
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(context, mapIntent, null)
        }
        holder.itemView.setOnClickListener{
            vm.doctor = data[position]
            navController(context as Activity).navigate(R.id.action_doctorsFragment_to_doctorFragment)
        }
    }

    class DoctorViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val doctorsPhoto = view.findViewById(R.id.doctors_photo) as ImageView
        val doctorsName = view.findViewById(R.id.doctors_name) as TextView
        val doctorsSpecialty = view.findViewById(R.id.doctors_specialty) as TextView
        val doctorsPhone = view.findViewById(R.id.doctors_phone) as TextView
        val doctorsAddress = view.findViewById(R.id.doctors_address) as TextView
        val doctorsDirections = view.findViewById(R.id.doctors_directions) as ImageView
    }

}

