package com.heer.multiplayer.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.heer.multiplayer.R
import com.heer.multiplayer.fragment.AboutFragment
import com.heer.multiplayer.fragment.LevelsFragment
import com.heer.multiplayer.fragment.MenuFragment
import com.heer.multiplayer.fragment.OnlineOfflineFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openMenuFragment()
    }

    private fun openMenuFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.fl, MenuFragment()).commit()
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.fl)) {
            is LevelsFragment -> openMenuFragment()
            is OnlineOfflineFragment -> openMenuFragment()
            is AboutFragment -> openMenuFragment()
            else -> super.onBackPressed()
        }
    }
}