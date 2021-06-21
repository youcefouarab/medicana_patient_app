package com.example.medicana.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicana.R
import com.example.medicana.util.CalendarUtils
import java.time.LocalDate

class CalendarAdapter(
        val context: Context,
        private val days: java.util.ArrayList<LocalDate?>,
        private val onItemListener: OnItemListener

): RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.layout_calendar, parent, false)
        return CalendarViewHolder(view, onItemListener, days)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        if (date == null) {
            holder.dayOfMonth.text = ""
        } else {
            holder.dayOfMonth.text = date.dayOfMonth.toString()
        }
        if (date == CalendarUtils.selectedDate) {
            holder.parentView.setBackgroundResource(R.drawable.bg_oval)
            holder.dayOfMonth.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun getItemCount() = days.size

}

interface OnItemListener {
    fun onItemClick(position: Int, date: LocalDate?)
}

class CalendarViewHolder(
    itemView: View,
    private val onItemListener: OnItemListener,
    days: ArrayList<LocalDate?>
): RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val days: ArrayList<LocalDate?>
    val parentView: View = itemView.findViewById(R.id.parentView)
    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)

    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, days[adapterPosition])
    }

    init {
        itemView.setOnClickListener(this)
        this.days = days
    }
}