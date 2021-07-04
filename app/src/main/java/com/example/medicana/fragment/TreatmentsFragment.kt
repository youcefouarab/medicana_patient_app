package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.medicana.R
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_treatments.*
import kotlinx.android.synthetic.main.layout_need_auth.*

class TreatmentsFragment : Fragment() {

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
            inflater.inflate(R.layout.fragment_treatments, container, false)
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.VISIBLE

        if (treatments_tabs != null) {
            selectTab(vm.treatCurr)

            treatments_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    selectTab(tab?.position)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }

        need_auth_button?.setOnClickListener {
            navController(requireActivity()).navigate(R.id.action_treatmentsFragment_to_authFragment)
        }

    }

    private fun selectTab(pos: Int?) {
        when (pos) {
            0 -> {
                val ft1 = parentFragmentManager.beginTransaction()
                ft1.replace(
                    R.id.treatments_framelayout,
                    CurrentTreatmentsFragment()
                )
                vm.treatCurr = 0
                ft1.commit()
            }
            1 -> {
                val ft2 = parentFragmentManager.beginTransaction()
                ft2.replace(R.id.treatments_framelayout, OldTreatmentsFragment())
                vm.treatCurr = 1
                ft2.commit()
            }
        }
        val tab: TabLayout.Tab? = treatments_tabs.getTabAt(pos!!)
        tab?.select()
    }
}