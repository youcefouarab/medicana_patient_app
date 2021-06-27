package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicana.R
import com.example.medicana.adapter.MyDoctorAdapter
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.room.RoomService
import com.example.medicana.util.navController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_advices.*
import kotlinx.android.synthetic.main.layout_need_auth.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.VISIBLE

        advices_doctors_list?.layoutManager = LinearLayoutManager(act)
        advices_doctors_list?.adapter = MyDoctorAdapter(act, RoomService.appDatabase.getDoctorDao().getMyDoctors())

        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_advicesFragment_to_authFragment)
        }

    }

}