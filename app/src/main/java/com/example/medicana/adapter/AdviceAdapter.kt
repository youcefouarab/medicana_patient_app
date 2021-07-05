package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicana.R
import com.example.medicana.entity.Advice
import com.example.medicana.util.displayDateFromUnix
import com.example.medicana.util.displayTimeFromUnix


class AdviceAdapter(val context: Context, val data: List<Advice>): RecyclerView.Adapter<AdviceAdapter.AdviceViewHolder>() {

    private val message = 0
    private val reply = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdviceViewHolder {
        return if (viewType == message) {
            AdviceViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.layout_advice_message, parent, false)
            )
        } else {
            AdviceViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.layout_advice_reply, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].message != null) {
            message
        } else {
            reply
        }
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdviceViewHolder, position: Int) {
        var displayDate = ""
        var displayTime = ""
        try {
            displayDate = displayDateFromUnix(data[position].date_time!!)
            displayTime = displayTimeFromUnix(data[position].date_time!!)
        } catch (t: Throwable) {

        }
        holder.adviceDateTime.text = "$displayDate - $displayTime"
        if (data[position].message != null) {
            holder.adviceMessage.text = data[position].message
        } else {
            holder.adviceMessage.text = data[position].reply
        }
    }

    class AdviceViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val adviceDateTime = view.findViewById(R.id.advice_datetime) as TextView
        val adviceMessage = view.findViewById(R.id.advice_message) as TextView
    }
}
