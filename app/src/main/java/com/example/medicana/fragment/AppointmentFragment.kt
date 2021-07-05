package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.room.RoomService
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.util.*
import com.example.medicana.viewmodel.VM.vm
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_appointment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        appointment_toolbar?.setupWithNavController(navController(act))
        appointment_toolbar?.title = ""

        if (appointment_bottom_sheet != null) {
            val bsBehave = BottomSheetBehavior.from(appointment_bottom_sheet)
            bsBehave.apply {
                peekHeight = 250
                this.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            bsBehave.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) dimmed_bg.visibility = View.GONE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    dimmed_bg.visibility = View.VISIBLE
                    dimmed_bg.alpha = slideOffset
                }
            })
        }

        val myAppointment = vm.myAppointment
        if (myAppointment != null) {
            appointment_doctor_name?.text = "Dr. " + myAppointment.first_name + " " + myAppointment.last_name
            appointment_doctor_specialty?.text = myAppointment.specialty
            if (myAppointment.photo != null) {
                Glide.with(act).load(BASE_URL + myAppointment.photo).into(appointment_doctor_photo)
            } else {
                if (myAppointment.gender == "male") {
                    Glide.with(act).load(R.drawable.default_doctor_male).into(appointment_doctor_photo)
                } else {
                    Glide.with(act).load(R.drawable.default_doctor_female).into(appointment_doctor_photo)
                }
            }
            appointment_call_button?.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + myAppointment.phone_number)
                    )
                )
            }
            appointment_map_button?.setOnClickListener {
                val mapIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + myAppointment.longitude + "," + myAppointment.latitude)
                )
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            var displayDate = ""
            try {
                displayDate = displayDate(myAppointment.date!!)
            } catch (t: Throwable) {

            }
            appointment_date?.text = displayDate
            appointment_time?.text = myAppointment.start_time
            appointment_cancel?.setOnClickListener {
                appointment_cancel?.isClickable = false
                val call = RetrofitService.endpoint.cancelAppointment(
                        myAppointment.appointment_id
                )
                call.enqueue(object : Callback<String> {
                    override fun onResponse(
                            call: Call<String>?,
                            response: Response<String>?
                    ) {
                        if (response?.isSuccessful == true) {
                            if (response.body() == RES_SUCCESS) {
                                RoomService.appDatabase.getAppointmentDao().deleteMyAppointment(myAppointment.appointment_id)
                                val count = RoomService.appDatabase.getAppointmentDao().checkAppointmentsWithDoctorExist(myAppointment.doctor_id)
                                if (count == 0) {
                                    RoomService.appDatabase.getDoctorDao().deleteMyDoctor(myAppointment.doctor_id)
                                }
                                Toast.makeText(act, R.string.booking_cancel, Toast.LENGTH_LONG).show()
                            } else {
                                checkFailure(act, null)
                            }
                        } else {
                            checkFailure(act, null)
                        }
                        navController(act).navigateUp()
                    }

                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        checkFailure(act, t)
                        navController(act).navigateUp()
                    }
                })
            }
            appointment_qr_code?.setImageBitmap(getQrCodeBitmap(myAppointment.appointment_id.toString()))
            val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
            val time = SimpleDateFormat("HH:mm").format(Date())
            try {
                if (myAppointment.date!! < date || (myAppointment.date == date + "T00:00:00.000Z" && myAppointment.finish_time!! < time)) {
                    appointment_bottom_sheet?.visibility = View.GONE
                }
            } catch (t: Throwable) {
                appointment_bottom_sheet?.visibility = View.GONE
            }
        }

    }

}