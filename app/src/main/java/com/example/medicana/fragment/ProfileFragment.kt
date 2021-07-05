package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.dimmed_bg
import kotlinx.android.synthetic.main.fragment_profile.profile_name
import kotlinx.android.synthetic.main.fragment_profile.profile_phone
import kotlinx.android.synthetic.main.layout_need_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

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
            inflater.inflate(R.layout.fragment_profile, container, false)
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.VISIBLE

        val pref = SharedPrefs(act)

        profile_name?.text = pref.firstName + " " + pref.lastName
        profile_phone?.text = pref.phoneNumber
        if (profile_photo != null) {
            Glide.with(act).load(R.drawable.default_profile).into(profile_photo)
        }
        if (profile_bottom_sheet != null) {
            val bsBehave = BottomSheetBehavior.from(profile_bottom_sheet)
            bsBehave.apply {
                peekHeight = 250
                this.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            bsBehave.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) dimmed_bg.visibility = View.GONE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    dimmed_bg.visibility = View.VISIBLE
                    dimmed_bg.alpha = slideOffset
                }
            })
        }

        profile_logout?.setOnClickListener {
            RoomService.appDatabase.getAdviceDao().deleteAll()
            RoomService.appDatabase.getAppointmentDao().deleteAll()
            RoomService.appDatabase.getDoctorDao().deleteAll()
            RoomService.appDatabase.getTreatmentDao().deleteAll()

            pref.connected = false
            pref.token = ""

            val call = RetrofitService.endpoint.unregisterToken(pref.deviceId)
            call.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>?,
                    response: Response<String>?
                ) {
                    if (response?.isSuccessful == false) {
                        checkFailure(act, null)
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    checkFailure(act, t)
                }
            })

            pref.deviceId = 0

            unsubscribeFromTreatments(pref.patientId)
            unsubscribeFromAdvice(pref.patientId)

            navController(act).navigate(R.id.action_profileFragment_to_nav_host)
        }

        need_auth_button?.setOnClickListener {
            navController(requireActivity()).navigate(R.id.action_profileFragment_to_authFragment)
        }

    }

    private fun unsubscribeFromTreatments(patient_id: Long) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("treatments-for-$patient_id")
                .addOnCompleteListener { task ->
                    var msg = "unsubscribed"
                    if (!task.isSuccessful) {
                        msg = "not unsubscribed"
                    }
                    Log.d("firebase", msg)
                }
    }

    private fun unsubscribeFromAdvice(patient_id: Long) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("give-advice-to-$patient_id")
                .addOnCompleteListener { task ->
                    var msg = "unsubscribed"
                    if (!task.isSuccessful) {
                        msg = "not unsubscribed"
                    }
                    Log.d("firebase", msg)
                }
    }
}