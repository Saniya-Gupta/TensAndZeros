package com.heer.multiplayer.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import com.heer.multiplayer.R
import com.heer.multiplayer.activity.OnlineActivity
import kotlinx.android.synthetic.main.fragment_online_users.*

class GameWinFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtGameStatus: TextView
    private lateinit var imgGameStatus: ImageView
    private lateinit var btnReplay: Button
    private lateinit var btnExit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_win, container, false)
        init(view)
        val players = sharedPreferences.getString("playing", "").toString()
        val player1 = players.substring(0, players.indexOf(":"))
        val player2 = players.substring(players.indexOf(":") + 1)
        val curPlayer = sharedPreferences.getString("userName", "").toString()
        myRef.child("playing")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(players).exists()) {
                        if (snapshot.child("exit").value == 0) {
                            if (snapshot.child("activePlayer").value == "draw") {
                                txtGameStatus.text = getString(R.string.draw)
                                imgGameStatus.setImageDrawable(activity?.let {
                                    ContextCompat.getDrawable(
                                        it, R.drawable.emoji_draw)
                                })
                            } else if (snapshot.child("activePlayer").value == curPlayer) {
                                txtGameStatus.text = getString(R.string.win)
                                imgGameStatus.setBackgroundResource(R.drawable.emoji_win)
                            } else {
                                txtGameStatus.text = getString(R.string.lost)
                                imgGameStatus.setBackgroundResource(R.drawable.emoji_lost)
                            }
                        } /*else {
                            if (snapshot.child("activePlayer").value == curPlayer) {
                                Toast.makeText(
                                    activity as AppCompatActivity,
                                    "You replayed the game",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                Toast.makeText(
                                    activity as AppCompatActivity,
                                    "${snapshot.child("activePlayer").value} replayed the game",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.fl, OnlineGameFragment())?.commit()
                        }*/
                    } else {
                        Toast.makeText(activity as AppCompatActivity, "Game Ended", Toast.LENGTH_SHORT)
                            .show()
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fl, MenuFragment())?.commit()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        btnReplay.setOnClickListener {
            myRef.child("playing").child(players).child(player1).setValue("")
            myRef.child("playing").child(players).child(player2).setValue("")
            myRef.child("playing").child(players).child("exit").setValue("1")
            myRef.child("playing").child(players).child("activePlayer")
                .setValue(curPlayer)
        }

        btnExit.setOnClickListener {
            myRef.child("playing").child(players).removeValue()
            myRef.child("users").child(player1).child("game_state").setValue("0")
            myRef.child("users").child(player2).child("game_state").setValue("0")
            sharedPreferences.edit().remove("playing").apply()
        }
        return view
    }

    private fun init(view: View) {
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.shared_pref),
            Context.MODE_PRIVATE
        )
        txtGameStatus = view.findViewById(R.id.txtGameStatus)
        imgGameStatus = view.findViewById(R.id.imgGameStatus)
        btnReplay = view.findViewById(R.id.btnReplay)
        btnExit = view.findViewById(R.id.btnExit)
        (activity as OnlineActivity).supportActionBar?.title = "Game ended"
    }
}