package com.example.germanflashcards.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.germanflashcards.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatDelegate
import android.widget.ImageButton
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        // Переключатель темы
        val btnToggleTheme = findViewById<ImageButton>(R.id.btnToggleTheme)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isNight = prefs.getBoolean("night_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isNight) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        btnToggleTheme.setOnClickListener {
            val newNight = !prefs.getBoolean("night_mode", false)
            prefs.edit().putBoolean("night_mode", newNight).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (newNight) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}