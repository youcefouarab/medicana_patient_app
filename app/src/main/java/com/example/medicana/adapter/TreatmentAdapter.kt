package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicana.R
import com.example.medicana.entity.Treatment
import com.example.medicana.fragment.AppointmentsFragmentDirections
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM


class TreatmentAdapter(val context: Context, val data: List<Treatment>): RecyclerView.Adapter<TreatmentAdapter.TreatmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentViewHolder {
        return TreatmentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_treatment, parent, false)
        )
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TreatmentViewHolder, position: Int) {
        holder.itemView.setOnClickListener{
            VM.vm.treatment = data[position]
            //navController(context as Activity).navigate(???)
        }
    }

    class TreatmentViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
}