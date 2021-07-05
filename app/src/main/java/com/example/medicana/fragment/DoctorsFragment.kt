package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicana.R
import com.example.medicana.adapter.DoctorAdapter
import com.example.medicana.util.*
import com.example.medicana.entity.Doctor
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.viewmodel.VM.vm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_doctors.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorsFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doctors, container, false)
    }

    override fun onResume() {
        super.onResume()
        act.nav_bottom?.visibility = View.VISIBLE
        doctors_list?.layoutManager = LinearLayoutManager(act)
        val data = vm.doctors
        if (data != null) {
            doctors_list?.adapter = DoctorAdapter(act, data)
        }
        val call = RetrofitService.endpoint.getDoctors()
        call.enqueue(object : Callback<List<Doctor>> {
            override fun onResponse(
                    call: Call<List<Doctor>>?,
                    response: Response<List<Doctor>>?
            ) {
                if (response?.isSuccessful == true) {
                    vm.doctors = response.body()
                    try {
                        doctors_list?.adapter = DoctorAdapter(act, vm.doctors!!)
                    } catch (t: Throwable) {

                    }
                } else {
                    checkFailure(act, null)
                }
            }

            override fun onFailure(call: Call<List<Doctor>>?, t: Throwable?) {
                checkFailure(act, t)
            }
        })
    }

}