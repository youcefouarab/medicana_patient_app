package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.medicana.MainViewModel
import com.example.medicana.R
import com.example.medicana.RES_SUCCESS
import com.example.medicana.SHARED_PREFS
import com.example.medicana.db.RoomService
import com.example.medicana.entity.Appointment
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.displayDate
import com.example.medicana.util.getQrCodeBitmap
import com.example.medicana.util.navController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_appointment.*
import kotlinx.android.synthetic.main.layout_need_auth.*
import kotlinx.android.synthetic.main.layout_new_appointment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AppointmentFragment : Fragment() {

    private val args: AppointmentFragmentArgs by navArgs()
    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View
        val connected =
            (act.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE))
            .getBoolean("connected", false)
        view = if (connected) {
            if (args.new) {
                inflater.inflate(R.layout.layout_new_appointment, container, false)
            } else {
                inflater.inflate(R.layout.fragment_appointment, container, false)
            }
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE

        val patient_id = (act.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE))
            .getLong("patient_id", 0)

        if (args.new) {
            new_appointment_toolbar?.setupWithNavController(navController(act))
            new_appointment_toolbar?.title = ""
            val doctor =
                (ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java))
                .doctor

            val appointment =
                (ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java))
                .appointment

            new_appointment_doctor?.text = "Dr. " + doctor?.first_name + " " + doctor?.last_name
            new_appointment_date?.text = displayDate(appointment?.date!!)
            new_appointment_time?.text = appointment?.time.toString()
            new_appointment_address?.text = doctor?.address
            new_appointment_confirm?.setOnClickListener {
                val call = RetrofitService.endpoint.bookAppointment(
                    appointment?.appointment_id,
                    patient_id
                )
                call.enqueue(object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>?,
                        response: Response<String>?
                    ) {
                        if (response?.isSuccessful!!) {
                            if (response?.body() == RES_SUCCESS) {
                                RoomService.appDatabase.getAppointmentDao().addMyAppointment(
                                    Appointment(
                                        appointment_id = appointment?.appointment_id,
                                        doctor_id = doctor?.doctor_id,
                                        date = appointment?.date,
                                        time = appointment?.time
                                    )
                                )
                                val count =
                                    RoomService.appDatabase.getDoctorDao().checkDoctorExists(
                                        doctor?.doctor_id
                                    )
                                if (count == 0) {
                                    RoomService.appDatabase.getDoctorDao().addMyDoctor(doctor!!)
                                }
                                Toast.makeText(act, R.string.booking_success, Toast.LENGTH_LONG)
                                    .show()
                                navController(act).navigate(R.id.action_appointmentFragment_to_appointmentsFragment)
                            } else {
                                Toast.makeText(act, R.string.error, Toast.LENGTH_LONG).show()
                                navController(act).navigateUp()
                            }
                        } else {
                            checkFailure(act)
                        }
                    }

                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        Log.e("Retrofit error", t.toString())
                        checkFailure(act)
                    }
                })
            }
        } else {
            appointment_toolbar?.setupWithNavController(navController(act))
            appointment_toolbar?.title = ""

            val bsBehave = BottomSheetBehavior.from(appointment_bottom_sheet!!)
            bsBehave.apply {
                peekHeight = 250
                this.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            bsBehave.setBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) dimmed_bg.visibility = View.GONE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    dimmed_bg.visibility = View.VISIBLE
                    dimmed_bg.alpha = slideOffset
                }
            })

            val myAppointment =
                (ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java))
                    .myAppointment

            profile_name?.text = "Dr. " + myAppointment?.first_name + " " + myAppointment?.last_name
            profile_phone?.text = myAppointment?.specialty

            //Glide.with(context).load(BASE_URL + data[position].photo).into(holder.doctors_photo)
            if (myAppointment?.gender == "male") Glide.with(act).load(R.drawable.default_doctor_male).into(
                appointment_doctor_photo!!
            )
            else Glide.with(act).load(R.drawable.default_doctor_female).into(
                appointment_doctor_photo!!
            )

            appointment_call_button?.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + myAppointment?.phone_number)
                    )
                )
            }
            appointment_map_button?.setOnClickListener {
                val mapIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + myAppointment?.longitude + "," + myAppointment?.latitude)
                )
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }

            appointment_date?.text = displayDate(myAppointment?.date!!)
            appointment_time?.text = myAppointment?.time

            appointment_cancel?.setOnClickListener {
                val call = RetrofitService.endpoint.cancelAppointment(
                    myAppointment?.appointment_id
                )
                call.enqueue(object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>?,
                        response: Response<String>?
                    ) {
                        if (response?.isSuccessful!!) {
                            if (response?.body() == RES_SUCCESS) {
                                RoomService.appDatabase.getAppointmentDao().deleteMyAppointment(
                                    myAppointment?.appointment_id
                                )
                                val count = RoomService.appDatabase.getAppointmentDao()
                                    .checkAppointmentsWithDoctorExist(
                                        myAppointment?.doctor_id
                                    )
                                if (count == 0) {
                                    RoomService.appDatabase.getDoctorDao().deleteMyDoctor(
                                        myAppointment?.doctor_id
                                    )
                                }
                                Toast.makeText(act, R.string.booking_cancel, Toast.LENGTH_LONG)
                                    .show()
                                navController(act).navigate(R.id.action_appointmentFragment_to_appointmentsFragment)
                            } else {
                                Toast.makeText(act, R.string.error, Toast.LENGTH_LONG).show()
                                navController(act).navigateUp()
                            }
                        } else {
                            checkFailure(act)
                        }
                    }

                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        Log.e("Retrofit error", t.toString())
                        checkFailure(act)
                    }
                })
            }

            appointment_qr_code?.setImageBitmap(getQrCodeBitmap(myAppointment?.appointment_id.toString()))

        }

        need_auth_toolbar?.setupWithNavController(navController(act))
        need_auth_toolbar?.title = ""
        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_appointmentFragment_to_authFragment)
        }
    }

}