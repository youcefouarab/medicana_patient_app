package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.ui.setupWithNavController
import com.example.medicana.R
import com.example.medicana.entity.*
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        auth_toolbar?.setupWithNavController(navController(act))
        auth_toolbar?.title = ""

        auth_login_button?.setOnClickListener {
            val phone = auth_phone.text.toString()
            auth_phone.text.clear()
            val password = auth_password.text.toString()
            auth_password.text.clear()
            val call = RetrofitService.endpoint.authPatient(phone, password)
            call.enqueue(object : Callback<Patient> {
                override fun onResponse(
                        call: Call<Patient>?,
                        response: Response<Patient>?
                ) {
                    if (response?.isSuccessful == true) {
                        val patient = response.body()
                        if (patient?.patient_id != null) {
                            val prefs = SharedPrefs(act)
                            prefs.connected = true
                            prefs.patientId = patient.patient_id
                            prefs.phoneNumber = patient.phone_number
                            prefs.firstName = patient.first_name
                            prefs.lastName = patient.last_name
                            reloadRoomDatabase(patient.patient_id)
                            registerToken(patient.patient_id)
                            subscribeToTreatments(patient.patient_id)
                            subscribeToAdvice(patient.patient_id)
                            Toast.makeText(act, R.string.welcome, Toast.LENGTH_LONG).show()
                            navController(act).navigateUp()
                        } else {
                            Toast.makeText(act, R.string.login_error, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        checkFailure(act, null)
                    }
                }

                override fun onFailure(call: Call<Patient>?, t: Throwable?) {
                    checkFailure(act, t)
                }
            })
        }
    }

    private fun reloadRoomDatabase(patient_id: Long) {
        val call1 = RetrofitService.endpoint.getMyAppointments(patient_id)
        call1.enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(
                    call: Call<List<Appointment>>?,
                    response: Response<List<Appointment>>?
            ) {
                if (response?.isSuccessful == true) {
                    val appointments = response.body()
                    val call2 = RetrofitService.endpoint.getMyDoctors(patient_id)
                    call2.enqueue(object : Callback<List<Doctor>> {
                        override fun onResponse(
                                call: Call<List<Doctor>>?,
                                response: Response<List<Doctor>>?
                        ) {
                            if (response?.isSuccessful == true) {
                                val doctors = response.body()
                                val call3 = RetrofitService.endpoint.getAllAdvice(patient_id)
                                call3.enqueue(object : Callback<List<Advice>> {
                                    override fun onResponse(
                                            call: Call<List<Advice>>?,
                                            response: Response<List<Advice>>?
                                    ) {
                                        if (response?.isSuccessful == true) {
                                            val advice = response.body()
                                            val call4 = RetrofitService.endpoint.getMyTreatments(patient_id)
                                            call4.enqueue(object : Callback<List<Treatment>> {
                                                override fun onResponse(
                                                        call: Call<List<Treatment>>?,
                                                        response: Response<List<Treatment>>?
                                                ) {
                                                    if (response?.isSuccessful == true) {
                                                        RoomService.appDatabase.getAppointmentDao().addMyAppointments(appointments)
                                                        RoomService.appDatabase.getDoctorDao().addMyDoctors(doctors)
                                                        RoomService.appDatabase.getAdviceDao().addMyAdvice(advice)
                                                        RoomService.appDatabase.getTreatmentDao().addMyTreatments(response.body())
                                                    } else {
                                                        checkFailure(act, null)
                                                    }
                                                }

                                                override fun onFailure(call: Call<List<Treatment>>?, t: Throwable?) {
                                                    checkFailure(act, t)
                                                }
                                            })
                                        } else {
                                            checkFailure(act, null)
                                        }
                                    }

                                    override fun onFailure(call: Call<List<Advice>>?, t: Throwable?) {
                                        checkFailure(act, t)
                                    }
                                })
                            } else {
                                checkFailure(act, null)
                            }
                        }

                        override fun onFailure(call: Call<List<Doctor>>?, t: Throwable?) {
                            checkFailure(act, t)
                        }
                    })
                } else {
                    checkFailure(act, null)
                }
            }

            override fun onFailure(call: Call<List<Appointment>>?, t: Throwable?) {
                checkFailure(act, t)
            }
        })

    }

    private fun registerToken(patient_id: Long) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            } else {
                val call = RetrofitService.endpoint.registerToken(
                    user_id = patient_id,
                    token = task.result
                )
                call.enqueue(object : Callback<Long> {
                    override fun onResponse(
                        call: Call<Long>?,
                        response: Response<Long>?
                    ) {
                        if (response?.isSuccessful == true) {
                            val prefs = SharedPrefs(act)
                            prefs.deviceId = response.body()!!
                            prefs.token = task.result
                        } else {
                            checkFailure(act, null)
                        }
                    }

                    override fun onFailure(call: Call<Long>?, t: Throwable?) {
                        checkFailure(act, t)
                    }
                })
            }
        })

    }

    private fun subscribeToTreatments(patient_id: Long) {
        FirebaseMessaging.getInstance().subscribeToTopic("treatments-for-$patient_id")
                .addOnCompleteListener { task ->
                    var msg = "subscribed"
                    if (!task.isSuccessful) {
                        msg = "not subscribed"
                    }
                    Log.d("firebase", msg)
                }
    }

    private fun subscribeToAdvice(patient_id: Long) {
        FirebaseMessaging.getInstance().subscribeToTopic("give-advice-to-$patient_id")
                .addOnCompleteListener { task ->
                    var msg = "subscribed"
                    if (!task.isSuccessful) {
                        msg = "not subscribed"
                    }
                    Log.d("firebase", msg)
                }
    }

}