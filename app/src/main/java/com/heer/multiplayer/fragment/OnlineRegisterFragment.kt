package com.heer.multiplayer.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.heer.multiplayer.R
import com.heer.multiplayer.activity.OnlineActivity
import java.util.regex.Pattern


class OnlineRegisterFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private lateinit var etUserName: EditText
    private lateinit var etUserPass: EditText
    private lateinit var btnOk: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        init(view)
        btnOk.setOnClickListener {
            if (validateUserNamePass()) {
                registerUser()
            }
        }
        return view
    }

    /* Java Regex is an API to define pattern searching or manipulating strings.
    *                 // \w	<- Metacharacter
                    // Any word character, short for [a-zA-Z_0-9] <- Character class
                    // Quantifiers: - *, +, ?, {}
                    // * for 0 or more times
                    // + for 1 or more times
                    // ? for 1 time
                    // {6} for 6 times only
                    // {x, } for x or more times
                    // {x, y} for atleat x times but less than y times
     */

    private fun validateUserNamePass(): Boolean {
        if (etUserName.text.isNotEmpty()) {
            if(etUserPass.text.isNotEmpty()) {
                if (etUserName.text.length < 20) {
                    if (etUserPass.text.length < 20) {
                        if (Pattern.matches("\\w*", etUserName.text)) {
                            if (Pattern.matches("\\d*", etUserPass.text)) {
                                return true
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Password can contain only digits",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "UserName can contain only letters, digits and underscore",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "Password length is 20",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else{
                        Toast.makeText(
                            activity,
                            "UserName length is 20",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            } else {
                Toast.makeText(activity, "Password cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "UserName cannot be empty!", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun init(view: View) {
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        etUserName = view.findViewById(R.id.etUserName)
        etUserPass = view.findViewById(R.id.etUserPass)
        btnOk = view.findViewById(R.id.btnOk)
        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.shared_pref),
            Context.MODE_PRIVATE
        )
        (activity as OnlineActivity).supportActionBar?.title = "Register | Login"
    }

    private fun registerUser() {
        myRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(etUserName.text.toString())) {
                    if(dataSnapshot.child(etUserName.text.toString()).child("password").value == etUserPass.text.toString()) {
                        loginUser()
                    } else {
                        Toast.makeText(
                            activity,
                            "User Name Exists and Password is Wrong!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    myRef.child("users").child(etUserName.text.toString()).child("game_state")
                        .setValue("0") // 0 -> inactive game-play
                    myRef.child("users").child(etUserName.text.toString()).child("password")
                        .setValue(etUserPass.text.toString()) // store password
                    loginUser()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error")
            }
        })
    }

    private fun loginUser() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("userName", etUserName.text.toString()).apply()
        sharedPreferences.edit().putString("userPass", etUserPass.text.toString()).apply()
        val activity = activity as AppCompatActivity
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fl, OnlineUsersFragment()).commit()
    }
}