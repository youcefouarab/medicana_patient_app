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
import com.example.medicana.adapter.TreatmentAdapter
import com.example.medicana.entity.Treatment
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import kotlinx.android.synthetic.main.fragment_old_treatments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class OldTreatmentsFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_old_treatments, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onResume() {
        super.onResume()
        old_treatments_list?.layoutManager = LinearLayoutManager(act)
        val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        old_treatments_list?.adapter = TreatmentAdapter(act, RoomService.appDatabase.getTreatmentDao().getMyOldTreatments(date + "T00:00:00.000Z"))
        val call = RetrofitService.endpoint.getMyTreatments(SharedPrefs(act).patientId)
        call.enqueue(object : Callback<List<Treatment>> {
            override fun onResponse(
                    call: Call<List<Treatment>>?,
                    response: Response<List<Treatment>>?
            ) {
                if (response?.isSuccessful == true) {
                    RoomService.appDatabase.getTreatmentDao().deleteAll()
                    RoomService.appDatabase.getTreatmentDao().addMyTreatments(response.body())
                    old_treatments_list?.adapter = TreatmentAdapter(act, RoomService.appDatabase.getTreatmentDao().getMyOldTreatments(date + "T00:00:00.000Z"))
                } else {
                    checkFailure(act, null)
                }
            }

            override fun onFailure(call: Call<List<Treatment>>?, t: Throwable?) {
                checkFailure(act, t)
            }
        })
    }

}