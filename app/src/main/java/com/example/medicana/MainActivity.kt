package com.example.medicana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.ui.NavigationUI
import com.example.medicana.util.navController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        NavigationUI.setupWithNavController(nav_bottom, navController(this))
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_host -> { true }
                R.id.appointmentsFragment -> { true }
                R.id.treatmentsFragment -> { true }
                R.id.advicesFragment -> { true }
                R.id.profileFragment -> { true }
                else -> false
            }
        }
        nav_bottom.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                R.id.nav_host -> { }
                R.id.appointmentsFragment -> { }
                R.id.treatmentsFragment -> { }
                R.id.advicesFragment -> { }
                R.id.profileFragment -> { }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController(this).navigateUp()
    }



}