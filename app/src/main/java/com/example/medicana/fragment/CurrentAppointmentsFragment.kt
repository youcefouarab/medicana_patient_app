package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicana.R
import com.example.medicana.adapter.AppointmentAdapter
import com.example.medicana.room.RoomService
import kotlinx.android.synthetic.main.fragment_current_appointments.*
import java.text.SimpleDateFormat
import java.util.*

class CurrentAppointmentsFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_current_appointments, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        current_appointments_list?.layoutManager = LinearLayoutManager(act)
        val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val time = SimpleDateFormat("HH:mm").format(Date())
        current_appointments_list?.adapter = AppointmentAdapter(act, RoomService.appDatabase.getAppointmentDao().getMyCurrentAppointments(date + "T00:00:00.000Z", time))
    }

}