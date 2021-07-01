package com.example.medicana

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.NavigationUI
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VM.context = this

        setContentView(R.layout.activity_main)
        NavigationUI.setupWithNavController(nav_bottom, navController(this))
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_host -> {
                    true
                }
                R.id.appointmentsFragment -> {
                    true
                }
                R.id.treatmentsFragment -> {
                    true
                }
                R.id.advicesFragment -> {
                    true
                }
                R.id.profileFragment -> {
                    true
                }
                else -> false
            }
        }
        nav_bottom.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                R.id.nav_host -> {
                }
                R.id.appointmentsFragment -> {
                }
                R.id.treatmentsFragment -> {
                }
                R.id.advicesFragment -> {
                }
                R.id.profileFragment -> {
                }
            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController(this).navigateUp()
    }



}