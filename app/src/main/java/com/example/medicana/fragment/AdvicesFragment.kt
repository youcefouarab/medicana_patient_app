package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicana.R
import com.example.medicana.adapter.MyDoctorAdapter
import com.example.medicana.entity.Advice
import com.example.medicana.entity.Doctor
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_advices.*
import kotlinx.android.synthetic.main.layout_need_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdvicesFragment : Fragment() {

private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val connected = SharedPrefs(act).connected
        return if (connected) {
            inflater.inflate(R.layout.fragment_advices, container, false)
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
    }

    override fun onResume() {
        super.onResume()
        act.nav_bottom?.visibility = View.VISIBLE

        advices_doctors_list?.layoutManager = LinearLayoutManager(act)
        advices_doctors_list?.adapter = MyDoctorAdapter(act, RoomService.appDatabase.getDoctorDao().getMyDoctors())

        val patientId = SharedPrefs(act).patientId
        val call1 = RetrofitService.endpoint.getMyDoctors(patientId)
        call1.enqueue(object : Callback<List<Doctor>> {
            override fun onResponse(
                    call: Call<List<Doctor>>?,
                    response: Response<List<Doctor>>?
            ) {
                if (response?.isSuccessful == true) {
                    val doctors = response.body()
                    val call2 = RetrofitService.endpoint.getAllAdvice(patientId)
                    call2.enqueue(object : Callback<List<Advice>> {
                        override fun onResponse(
                                call: Call<List<Advice>>?,
                                response: Response<List<Advice>>?
                        ) {
                            if (response?.isSuccessful == true) {
                                RoomService.appDatabase.getDoctorDao().deleteAll()
                                RoomService.appDatabase.getDoctorDao().addMyDoctors(doctors)
                                RoomService.appDatabase.getAdviceDao().deleteAll()
                                RoomService.appDatabase.getAdviceDao().addMyAdvice(response.body())
                                advices_doctors_list?.adapter = MyDoctorAdapter(act, RoomService.appDatabase.getDoctorDao().getMyDoctors())
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

        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_advicesFragment_to_authFragment)
        }
    }
}