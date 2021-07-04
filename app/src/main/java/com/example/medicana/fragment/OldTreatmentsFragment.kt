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
import com.example.medicana.room.RoomService
import kotlinx.android.synthetic.main.fragment_old_treatments.*
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_old_treatments, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        old_treatments_list?.layoutManager = LinearLayoutManager(act)

        val date = SimpleDateFormat("yyyy-MM-dd").format(Date())

        old_treatments_list?.adapter = TreatmentAdapter(act, RoomService.appDatabase.getTreatmentDao().getMyOldTreatments(date + "T00:00:00.000Z"))
    }

}