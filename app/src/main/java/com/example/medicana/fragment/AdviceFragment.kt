package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.adapter.AdviceAdapter
import com.example.medicana.entity.Advice
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.service.AdviceAddSyncService
import com.example.medicana.util.BASE_URL
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_advice.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdviceFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advice, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        act.nav_bottom?.visibility = View.GONE
        advice_toolbar?.setupWithNavController(navController(act))
        advice_toolbar?.title = ""

        val doctor = vm.doctor
        advice_doctor_name?.text = "Dr. " + doctor?.first_name + " " + doctor?.last_name
        advice_doctor_specialty?.text = doctor?.specialty
        if (doctor?.photo != null) {
            Glide.with(act).load(BASE_URL + doctor.photo).into(advice_doctor_photo)
        } else {
            if (doctor?.gender == "male") {
                Glide.with(act).load(R.drawable.default_doctor_male).into(advice_doctor_photo)
            } else {
                Glide.with(act).load(R.drawable.default_doctor_female).into(advice_doctor_photo)
            }
        }

        advice_list?.layoutManager = LinearLayoutManager(act).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        reload(false)
        Handler(Looper.getMainLooper()).postDelayed({
            reload(true)
        },10000)

        advice_send?.setOnClickListener {
            val message = advice_message_to_send?.text.toString()
            if (message.isNotBlank()) {
                advice_message_to_send?.text?.clear()
                RoomService.appDatabase.getAdviceDao().addAdvice(
                    Advice(
                        doctor_id = doctor?.doctor_id,
                        patient_id = SharedPrefs(act).patientId,
                        message = message,
                    )
                )
                scheduleSync()
                reload(false)
            }
        }
    }

    private fun reload(withState: Boolean) {
        val doctor = vm.doctor
        val recyclerViewState1 = advice_list?.layoutManager?.onSaveInstanceState()
        advice_list?.adapter = AdviceAdapter(act, RoomService.appDatabase.getAdviceDao().getAdviceWithDoctor(doctor?.doctor_id))
        if (withState) advice_list?.layoutManager?.onRestoreInstanceState(recyclerViewState1)

        val call = RetrofitService.endpoint.getAdviceWithDoctor(doctor?.doctor_id, SharedPrefs(act).patientId)
        call.enqueue(object : Callback<List<Advice>> {
            override fun onResponse(
                call: Call<List<Advice>>?,
                response: Response<List<Advice>>?
            ) {
                if (response?.isSuccessful == true) {
                    RoomService.appDatabase.getAdviceDao().addMyAdvice(response.body())
                    val recyclerViewState2 = advice_list?.layoutManager?.onSaveInstanceState()
                    val advice = RoomService.appDatabase.getAdviceDao().getAdviceWithDoctor(doctor?.doctor_id)
                    val oldCount = advice_list?.adapter?.itemCount
                    advice_list?.adapter = AdviceAdapter(act, advice)
                    if (withState || advice.size <= oldCount!!) advice_list?.layoutManager?.onRestoreInstanceState(recyclerViewState2)
                } else {
                    checkFailure(act, null)
                }
            }

            override fun onFailure(call: Call<List<Advice>>?, t: Throwable?) {
                checkFailure(act, t)
            }
        })
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder().
        setRequiredNetworkType(NetworkType.CONNECTED).build()
        val req= OneTimeWorkRequest.Builder(AdviceAddSyncService::class.java).
        setConstraints(constraints).addTag("patient_advice_add_constraints").build()
        val workManager = WorkManager.getInstance(act)
        workManager.enqueueUniqueWork("patient_advice_add_work", ExistingWorkPolicy.REPLACE, req)
    }
}