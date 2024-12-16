package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.RootActivityBinding

class RootActivity : AppCompatActivity() {
    private lateinit var binding: RootActivityBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RootActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentCreatePlaylist -> hideBottomNav()
                else -> showBottomNav()
            }
        }
    }

    private fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE
    }

    private fun showBottomNav() {
        bottomNavigationView.visibility = View.VISIBLE
    }
}