package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.adapter.CalendarAdapter
import com.example.medicana.adapter.TimeAdapter
import com.example.medicana.util.*
import com.example.medicana.entity.Appointment
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.util.CalendarUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_doctor.*
import java.time.LocalDate

import com.example.medicana.util.CalendarUtils.daysInWeekArray
import com.example.medicana.util.CalendarUtils.monthYearFromDate
import com.example.medicana.viewmodel.VM.vm
import kotlinx.android.synthetic.main.fragment_appointment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class DoctorFragment : Fragment(), CalendarAdapter.OnItemListener {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doctor, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        doctor_toolbar?.setupWithNavController(navController(act))
        doctor_toolbar?.title = ""

        val doctor = vm.doctor
        if (doctor?.photo != null) {
            Glide.with(act).load(BASE_URL + doctor.photo).into(doctor_photo)
        } else {
            if (doctor?.gender == "male") {
                Glide.with(act).load(R.drawable.default_doctor_male).into(doctor_photo)
            } else {
                Glide.with(act).load(R.drawable.default_doctor_female).into(doctor_photo)
            }
        }
        doctor_name?.text = "Dr. " + doctor?.first_name + " " + doctor?.last_name
        doctor_specialty?.text = doctor?.specialty
        doctor_call_button?.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + doctor?.phone_number)
                )
            )
        }
        doctor_map_button.setOnClickListener {
            val mapIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + doctor?.longitude + "," + doctor?.latitude)
            )
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        setWeekView()

        calendar_next?.setOnClickListener {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1)
            setWeekView()
        }
        calendar_previous?.setOnClickListener {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1)
            setWeekView()
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun setWeekView() {
        val selectedDate = CalendarUtils.selectedDate
        val selectedDateString = selectedDate.toString()
        month_year?.text = monthYearFromDate(selectedDate)
        val days = daysInWeekArray(selectedDate)
        calendar_recyclerview?.layoutManager = GridLayoutManager(act, 7)
        calendar_recyclerview?.adapter = CalendarAdapter(act, days, this)
        times_recyclerview?.layoutManager = GridLayoutManager(act, 3)
        val doctor = vm.doctor
        val today = SimpleDateFormat("yyyy-MM-dd").format(Date())
        var time = SimpleDateFormat("HH:mm").format(Date())
        if (selectedDateString > today) time = "00:00"
        if ((doctor != null) && (today <= selectedDateString)) {
            val call = RetrofitService.endpoint.getAvailabilitiesForDoctorByDate(
                doctor.doctor_id,
                selectedDateString,
                time
            )
            call.enqueue(object : Callback<List<Appointment>> {
                override fun onResponse(
                        call: Call<List<Appointment>>?,
                        response: Response<List<Appointment>>?
                ) {
                    if (response?.isSuccessful == true) {
                        try {
                            times_recyclerview?.adapter = TimeAdapter(act, response.body()!!)
                        } catch (t: Throwable) {
                            times_recyclerview?.adapter = TimeAdapter(act, ArrayList<Appointment>())
                        }
                    } else {
                        checkFailure(act, null)
                    }
                }
                override fun onFailure(call: Call<List<Appointment>>?, t: Throwable?) {
                    checkFailure(act, t)
                }
            })
        } else {
            times_recyclerview?.adapter = TimeAdapter(act, ArrayList<Appointment>())
        }

    }

    override fun onItemClick(position: Int, date: LocalDate?) {
        CalendarUtils.selectedDate = date!!
        setWeekView()
    }

}