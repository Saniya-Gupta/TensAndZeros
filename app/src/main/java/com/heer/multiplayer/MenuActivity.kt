package com.heer.multiplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    fun startGameOnline(view: View) {
        startActivity(Intent(this@MenuActivity, OnlineLoginActivity::class.java))
    }

    fun startSinglePlayerGame(view: View) {
        startActivity(Intent(this@MenuActivity, GameActivity::class.java))
    }

}