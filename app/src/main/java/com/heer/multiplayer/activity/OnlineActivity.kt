package com.heer.multiplayer.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.heer.multiplayer.R
import com.heer.multiplayer.fragment.*

class OnlineActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        if (sharedPreferences.getBoolean("isLoggedIn", false))
            supportFragmentManager.beginTransaction().replace(R.id.fl, OnlineUsersFragment())
                .commit()
        else
            supportFragmentManager.beginTransaction().replace(R.id.fl, LoginFragment()).commit()
    }

}