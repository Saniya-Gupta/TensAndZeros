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


class LoginFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private lateinit var etUserName: EditText
    private lateinit var btnOk: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        init(view)
        btnOk.setOnClickListener {
            registerUser(etUserName.text.toString())
        }
        return view
    }


    private fun init(view: View) {
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        etUserName = view.findViewById(R.id.etUserName)
        btnOk = view.findViewById(R.id.btnOk)
        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.shared_pref),
            Context.MODE_PRIVATE
        )
    }

    private fun registerUser(etUserName: String) {
        val activity = activity as AppCompatActivity
        myRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(etUserName)) {
                    Toast.makeText(activity, "User Name Occupied!", Toast.LENGTH_SHORT).show()
                } else {
                    myRef.child("users").child(etUserName).setValue("")
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                    sharedPreferences.edit().putString("userName", etUserName).apply()
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fl, OnlineUsersFragment()).commit()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error")
            }
        })
    }
}