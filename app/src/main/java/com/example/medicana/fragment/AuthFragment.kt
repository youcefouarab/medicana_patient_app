package com.example.medicana.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.navigation.ui.setupWithNavController
import com.example.medicana.R
import com.example.medicana.SHARED_PREFS
import com.example.medicana.entity.Patient
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import com.example.medicana.util.reloadRoomDatabase
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
        // Inflate the layout for this fragment
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
                    if (response?.isSuccessful!!) {
                        val patient = response.body()
                        if (patient?.patient_id != null) {
                            val pref = act.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                            pref.edit {
                                putBoolean("connected", true)
                                putLong("patient_id", patient.patient_id)
                                putLong("user_id", patient.user_id!!)
                                putString("phone_number", patient.phone_number)
                                putString("first_name", patient.first_name)
                                putString("last_name", patient.last_name)
                                putString("address", patient.address)
                                putString("gender", patient.gender)
                                putString("photo", patient.photo)
                            }
                            reloadRoomDatabase(patient.patient_id, act)
                            Toast.makeText(act, R.string.welcome, Toast.LENGTH_LONG).show()
                            navController(act).navigateUp()
                        } else {
                            Toast.makeText(act, R.string.login_error, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        checkFailure(act)
                    }
                }

                override fun onFailure(call: Call<Patient>?, t: Throwable?) {
                    Log.e("Retrofit error", t.toString())
                    checkFailure(act)
                }
            })
        }
    }

}