package com.practicum.playlistmaker.main.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.RootActivityBinding

class RootActivity : AppCompatActivity() {
    private lateinit var binding: RootActivityBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private var currentScreen: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding = RootActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentCreatePlaylist, R.id.playlistInfoFragment, R.id.trackInfoFragment -> {
                    hideBottomNav()
                    currentScreen = destination.id
                }

                else -> {
                    showBottomNav()
                    currentScreen = destination.id
                }
            }
        }
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            // Если высота клавиатуры больше 100 пикселей, считаем, что клавиатура отображается
            if (keypadHeight > 100) {
                hideBottomNav()
            } else {
                handleBottomNavVisibility()
            }
        }
    }

    private fun handleBottomNavVisibility() {
        // Проверяем currentScreen, если текущий экран требует скрытия bottom nav, скрываем
        when (currentScreen) {
            R.id.fragmentCreatePlaylist, R.id.playlistInfoFragment, R.id.trackInfoFragment -> {
                hideBottomNav()
            }

            else -> {
                showBottomNav()
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