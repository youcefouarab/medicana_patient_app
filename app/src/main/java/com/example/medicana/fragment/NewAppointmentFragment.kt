package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupWithNavController
import com.example.medicana.R
import com.example.medicana.util.RES_SUCCESS
import com.example.medicana.entity.Appointment
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.displayDate
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM
import com.example.medicana.viewmodel.VM.vm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_new_appointment.*
import kotlinx.android.synthetic.main.layout_need_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewAppointmentFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (SharedPrefs(act).connected) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                act.window.statusBarColor = ContextCompat.getColor(act, R.color.white)
            }
            inflater.inflate(R.layout.fragment_new_appointment, container, false)
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        new_appointment_toolbar?.setupWithNavController(navController(act))
        new_appointment_toolbar?.title = ""

        val doctor = VM.vm.doctor
        val appointment = VM.vm.appointment
        if ((doctor != null) && (appointment != null)) {
            new_appointment_doctor?.text = "Dr. " + doctor.first_name + " " + doctor.last_name
            try {
                new_appointment_date?.text = displayDate(appointment.date!!)
            } catch (t: Throwable) {

            }
            new_appointment_time?.text = appointment.start_time
            new_appointment_address?.text = doctor.address
            new_appointment_confirm?.setOnClickListener {
                new_appointment_confirm?.isClickable = false
                val call = RetrofitService.endpoint.bookAppointment(
                    appointment.appointment_id,
                    SharedPrefs(act).patientId,
                    appointment.doctor_id
                )
                call.enqueue(object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>?,
                        response: Response<String>?
                    ) {
                        if (response?.isSuccessful == true) {
                            if (response.body() == RES_SUCCESS) {
                                RoomService.appDatabase.getAppointmentDao().addMyAppointment(
                                    Appointment(
                                        appointment_id = appointment.appointment_id,
                                        doctor_id = appointment.doctor_id,
                                        date = appointment.date,
                                        start_time = appointment.start_time,
                                        finish_time = appointment.finish_time
                                    )
                                )
                                RoomService.appDatabase.getDoctorDao().addMyDoctor(doctor)
                                Toast.makeText(act, R.string.booking_success, Toast.LENGTH_LONG).show()
                                vm.appointCurr = 0
                                navController(act).navigate(R.id.action_newAppointmentFragment_to_appointmentsFragment)
                            } else {
                                checkFailure(act, null)
                                navController(act).navigateUp()
                            }
                        } else {
                            checkFailure(act, null)
                            navController(act).navigateUp()
                        }
                    }

                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        checkFailure(act, t)
                        navController(act).navigateUp()
                    }
                })
            }
        }

        need_auth_toolbar?.setupWithNavController(navController(act))
        need_auth_toolbar?.title = ""
        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_newAppointmentFragment_to_authFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            act.window.statusBarColor = ContextCompat.getColor(act, R.color.medicana)
        }
    }
}