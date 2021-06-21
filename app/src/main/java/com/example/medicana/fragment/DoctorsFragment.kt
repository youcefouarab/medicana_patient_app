package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicana.R
import com.example.medicana.adapter.DoctorAdapter
import com.example.medicana.util.*
import com.example.medicana.entity.Doctor
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.MainViewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.VISIBLE
        doctors_list?.layoutManager = LinearLayoutManager(act)
        val data =
            ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java)
            .doctors
        if (data != null) {
            doctors_list?.adapter = DoctorAdapter(act, data)
        } else {
            val call = RetrofitService.endpoint.getDoctors()
            call.enqueue(object : Callback<List<Doctor>> {
                override fun onResponse(
                    call: Call<List<Doctor>>?,
                    response: Response<List<Doctor>>?
                ) {
                    if (response?.isSuccessful!!) {
                        val doctors = response.body()
                        try {
                            ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java)
                                .doctors = doctors
                        } catch (e: Exception) {
                            Log.e("DoctorViewModel error", e.toString())
                        }
                        doctors_list?.layoutManager = LinearLayoutManager(act)
                        doctors_list?.adapter = DoctorAdapter(act, doctors!!)
                    } else {
                        checkFailure(act)
                    }
                }

                override fun onFailure(call: Call<List<Doctor>>?, t: Throwable?) {
                    Log.e("Retrofit error", t.toString())
                    checkFailure(act)
                }
            })
        }
    }



}