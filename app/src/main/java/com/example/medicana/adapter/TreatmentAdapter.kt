package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicana.R
import com.example.medicana.entity.Treatment
import com.example.medicana.util.displayDate
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
        holder.treatmentDescription.text = data[position].description
        holder.treatmentStartDate.text = displayDate(data[position].start_date!!)
        holder.treatmentFinishDate.text = displayDate(data[position].finish_date!!)

        holder.itemView.setOnClickListener{
            VM.vm.treatment = data[position]
            //navController(context as Activity).navigate(???)
        }
    }

    class TreatmentViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val treatmentStartDate = view.findViewById(R.id.treatments_start_date) as TextView
        val treatmentFinishDate = view.findViewById(R.id.treatments_finish_date) as TextView
        val treatmentDescription = view.findViewById(R.id.treatments_description) as TextView
    }
}