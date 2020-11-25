package com.heer.multiplayer.activity

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
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

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {

            // Remove user from playing root if exists in it due to abnormal closure of game
            myRef.child("playing").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val players = snapshot.children
                    players.forEach {
                        val temp = it.key.toString()
                        sharedPreferences.getString("userName", "")?.let { it1 ->
                            removePlayerFromPlaying(
                                temp,
                                it1
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            supportFragmentManager.beginTransaction().replace(R.id.fl, OnlineUsersFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fl, OnlineRegisterFragment())
                .commit()
        }
    }

    private fun removePlayerFromPlaying(temp: String, userName: String) {
        val colonIndex = temp.indexOf(':')
        val player1 = temp.substring(0, colonIndex)
        val player2 = temp.substring(colonIndex + 1)
        if (userName == player1 || userName == player2) {
            myRef.child("playing").child(temp).removeValue()
            myRef.child("users").child(player1).child("game_state").setValue("0")
            myRef.child("users").child(player2).child("game_state").setValue("0")
            sharedPreferences.edit().remove("playing").apply()
        }
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.fl)) {
            is OnlineGameFragment -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("End Game")
                dialog.setMessage("Are you sure?")
                dialog.setPositiveButton("Yes") { _, _ ->
                    val players = sharedPreferences.getString("playing", "").toString()
                    val colonIndex = players.indexOf(':')
                    val player1 = players.substring(0, colonIndex)
                    val player2 = players.substring(colonIndex + 1)
                    myRef.child("playing").child("$player1:$player2")
                        .removeValue()
                    myRef.child("users").child(player1).child("game_state")
                        .setValue("0")
                    myRef.child("users").child(player2).child("game_state")
                        .setValue("0")
                    sharedPreferences.edit().remove("playing").apply()
                    finish()
                }
                dialog.create().show()
            }
            else -> super.onBackPressed()
        }
    }

}