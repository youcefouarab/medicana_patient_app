package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.entity.Doctor
import com.example.medicana.util.navController
import com.example.medicana.MainViewModel


class DoctorAdapter(val context: Context, val data: List<Doctor>): RecyclerView.Adapter<DoctorViewHolder>() {

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
        holder.doctorsAddress.text = data[position].address

        //Glide.with(context).load(BASE_URL + data[position].photo).into(holder.doctors_photo)
        if (data[position].gender == "male") Glide.with(context).load(R.drawable.default_doctor_male).into(
            holder.doctorsPhoto
        )
        else Glide.with(context).load(R.drawable.default_doctor_female).into(holder.doctorsPhoto)

        holder.itemView.setOnClickListener{
            (ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java)).doctor = data[position]
            navController(context as Activity).navigate(R.id.action_doctorsFragment_to_doctorFragment)
        }
    }
}

class DoctorViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val doctorsPhoto = view.findViewById(R.id.doctors_photo) as ImageView
    val doctorsName = view.findViewById(R.id.doctors_name) as TextView
    val doctorsSpecialty = view.findViewById(R.id.doctors_specialty) as TextView
    val doctorsAddress = view.findViewById(R.id.doctors_address) as TextView
}