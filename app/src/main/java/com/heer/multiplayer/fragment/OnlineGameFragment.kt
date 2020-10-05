package com.heer.multiplayer.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.heer.multiplayer.R

class OnlineGameFragment : Fragment(), View.OnClickListener {

    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private lateinit var img4: ImageView
    private lateinit var img5: ImageView
    private lateinit var img6: ImageView
    private lateinit var img7: ImageView
    private lateinit var img8: ImageView
    private lateinit var img9: ImageView
    private lateinit var txtPlayerTurn: TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var player1: String = ""
    private var player2: String = ""
    private var anotherPlayer = ""
    private var currentPlayer = ""
    private var movesCurrentPlayer = ""
    private var gameState = 1 // 1 -> Game Active 0 -> Game won or draw

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_online_game, container, false)
        init(view)
        enableClick()
        prepareGameSpace()
        return view
    }

    private fun init(view: View) {
        img1 = view.findViewById(R.id.img1)
        img1.setOnClickListener(this)
        img2 = view.findViewById(R.id.img2)
        img2.setOnClickListener(this)
        img3 = view.findViewById(R.id.img3)
        img3.setOnClickListener(this)
        img4 = view.findViewById(R.id.img4)
        img4.setOnClickListener(this)
        img5 = view.findViewById(R.id.img5)
        img5.setOnClickListener(this)
        img6 = view.findViewById(R.id.img6)
        img6.setOnClickListener(this)
        img7 = view.findViewById(R.id.img7)
        img7.setOnClickListener(this)
        img8 = view.findViewById(R.id.img8)
        img8.setOnClickListener(this)
        img9 = view.findViewById(R.id.img9)
        img9.setOnClickListener(this)
        txtPlayerTurn = view.findViewById(R.id.txtPlayerTurn)
        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.shared_pref),
            Context.MODE_PRIVATE
        )
        val players = sharedPreferences.getString("playing", "").toString()
        player1 = players.substring(0, players.indexOf(":"))
        player2 = players.substring(players.indexOf(":") + 1)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        currentPlayer = sharedPreferences.getString("userName", "").toString()
        anotherPlayer = if (currentPlayer == player1)
            player2
        else
            player1
    }

    private fun enableClick() {
        img1.isEnabled = true
        img2.isEnabled = true
        img3.isEnabled = true
        img4.isEnabled = true
        img5.isEnabled = true
        img6.isEnabled = true
        img7.isEnabled = true
        img8.isEnabled = true
        img9.isEnabled = true
    }

    private fun disableClick() {
        img1.isEnabled = false
        img2.isEnabled = false
        img3.isEnabled = false
        img4.isEnabled = false
        img5.isEnabled = false
        img6.isEnabled = false
        img7.isEnabled = false
        img8.isEnabled = false
        img9.isEnabled = false
    }

    private fun prepareGameSpace() {
        myRef.child("users").child("playing")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        if (snapshot.child("$player1:$player2").exists()) {
                            movesCurrentPlayer = snapshot.child("$player1:$player2")
                                .child(currentPlayer).value.toString()
                            val movesAnotherPlayer = snapshot.child("$player1:$player2")
                                .child(anotherPlayer).value.toString()
                            checkWinner(movesCurrentPlayer, movesAnotherPlayer)
                            gameState = Integer.parseInt(
                                snapshot.child("$player1:$player2").child("exit").value.toString()
                            )
                            val activeStatePlayer = snapshot.child("$player1:$player2")
                                .child("activePlayer").value.toString()
                            if (gameState == 0) {
                                show(activeStatePlayer)
                            } else if (activeStatePlayer == currentPlayer) {
                                disableClickCurrentPlayer(movesCurrentPlayer, currentPlayer)
                                disableClickCurrentPlayer(movesAnotherPlayer, anotherPlayer)
                            } else {
                                disableClickCurrentPlayer(movesCurrentPlayer, currentPlayer)
                                disableClickCurrentPlayer(movesAnotherPlayer, anotherPlayer)
                                disableClick()
                            }
                        } else {
                            Toast.makeText(activity, "Game ended", Toast.LENGTH_SHORT).show()
                            activity!!.finish()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onClick(v: View?) {
        disableClick()
        val imgTapped: ImageView = v as ImageView
        if (player1 == currentPlayer) {
            when (imgTapped.id) {
                R.id.img1 -> {
                    img1.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 1
                }
                R.id.img2 -> {
                    img2.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 2
                }
                R.id.img3 -> {
                    img3.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 3
                }
                R.id.img4 -> {
                    img4.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 4
                }
                R.id.img5 -> {
                    img5.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 5
                }
                R.id.img6 -> {
                    img6.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 6
                }
                R.id.img7 -> {
                    img7.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 7
                }
                R.id.img8 -> {
                    img8.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 8
                }
                else -> {
                    img9.setImageResource(R.drawable.cross)
                    movesCurrentPlayer += 9
                }
            }
            myRef.child("users").child("playing").child("$player1:$player2")
                .child(currentPlayer).setValue(movesCurrentPlayer)
            myRef.child("users").child("playing").child("$player1:$player2")
                .child("activePlayer").setValue(anotherPlayer)
        } else {
            when (imgTapped.id) {
                R.id.img1 -> {
                    img1.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 1
                }
                R.id.img2 -> {
                    img2.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 2
                }
                R.id.img3 -> {
                    img3.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 3
                }
                R.id.img4 -> {
                    img4.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 4
                }
                R.id.img5 -> {
                    img5.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 5
                }
                R.id.img6 -> {
                    img6.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 6
                }
                R.id.img7 -> {
                    img7.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 7
                }
                R.id.img8 -> {
                    img8.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 8
                }
                else -> {
                    img9.setImageResource(R.drawable.circle)
                    movesCurrentPlayer += 9
                }
            }
            myRef.child("users").child("playing").child("$player1:$player2")
                .child(currentPlayer).setValue(movesCurrentPlayer)
            myRef.child("users").child("playing").child("$player1:$player2")
                .child("activePlayer").setValue(anotherPlayer)
        }

    }

    private fun checkWinner(movesCurrentPlayer: String, movesAnotherPlayer: String) {
        val winningPositions = arrayOf(
            arrayOf(1, 2, 3), arrayOf(4, 5, 6), arrayOf(7, 8, 9),
            arrayOf(1, 4, 7), arrayOf(2, 5, 8), arrayOf(3, 6, 9),
            arrayOf(1, 5, 9), arrayOf(3, 5, 7)
        )
        for (i in winningPositions) {
            var flag = 1
            for (j in i) {
                if (movesCurrentPlayer.contains("$j"))
                    continue
                else {
                    flag = 0
                    break
                }
            }
            if (movesAnotherPlayer.length + movesCurrentPlayer.length >= 9) {
                myRef.child("users").child("playing").child("$player1:$player2")
                    .child("activePlayer").setValue("draw")
                myRef.child("users").child("playing").child("$player1:$player2")
                    .child("exit")
                    .setValue(0)
            }
            if (flag == 1) {
                myRef.child("users").child("playing").child("$player1:$player2")
                    .child("activePlayer").setValue("$currentPlayer:$anotherPlayer")
                myRef.child("users").child("playing").child("$player1:$player2").child("exit")
                    .setValue(0)
            }
        }
    }

    private fun show(msg: String) {
        Log.d("Tag", "msg = $msg")
        val display = if (msg == "draw") {
            "Draw"
        } else {
            "${msg.substring(0, msg.indexOf(":"))} has won against ${msg.substring(msg.indexOf(":") + 1)}"
        }
        if (activity != null) {
            AlertDialog.Builder(activity).setTitle("New Game?")
                .setMessage(display)
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    if(display == "Draw")
                        replayGame(player1)
                    else {
                        replayGame(display.substring(0, display.indexOf(" ")))
                    }
                }
                .setNegativeButton("No") { _, _ ->
                    resetGame()
                    activity?.finish()
                }
                .create()
                .show()
        }
    }

    private fun resetGame() {
        myRef.child("users").child(player1).setValue("")
        myRef.child("users").child(player2).setValue("")
        myRef.child("users").child("playing").child("$player1:$player2").removeValue()
        sharedPreferences.edit().remove("playing").apply()
    }

    private fun replayGame(firstPlayer: String) {
        myRef.child("users").child("playing").child("$player1:$player2").child(player1).setValue("")
        myRef.child("users").child("playing").child("$player1:$player2").child(player2).setValue("")
        myRef.child("users").child("playing").child("$player1:$player2").child("exit").setValue(1)
        myRef.child("users").child("playing").child("$player1:$player2").child("activePlayer")
            .setValue(firstPlayer)
    }

    private fun disableClickCurrentPlayer(moves: String, player: String) {
        for (i in moves) {
            if (player1 == player) {
                when (Integer.parseInt(i + "")) {
                    1 -> {
                        img1.isEnabled = false
                        img1.setImageResource(R.drawable.cross)
                    }
                    2 -> {
                        img2.isEnabled = false
                        img2.setImageResource(R.drawable.cross)
                    }
                    3 -> {
                        img3.isEnabled = false
                        img3.setImageResource(R.drawable.cross)
                    }
                    4 -> {
                        img4.isEnabled = false
                        img4.setImageResource(R.drawable.cross)
                    }
                    5 -> {
                        img5.isEnabled = false
                        img5.setImageResource(R.drawable.cross)
                    }
                    6 -> {
                        img6.isEnabled = false
                        img6.setImageResource(R.drawable.cross)
                    }
                    7 -> {
                        img7.isEnabled = false
                        img7.setImageResource(R.drawable.cross)
                    }
                    8 -> {
                        img8.isEnabled = false
                        img8.setImageResource(R.drawable.cross)
                    }
                    else -> {
                        img9.isEnabled = false
                        img9.setImageResource(R.drawable.cross)
                    }
                }
            } else {
                when (Integer.parseInt(i + "")) {
                    1 -> {
                        img1.isEnabled = false
                        img1.setImageResource(R.drawable.circle)
                    }
                    2 -> {
                        img2.isEnabled = false
                        img2.setImageResource(R.drawable.circle)
                    }
                    3 -> {
                        img3.isEnabled = false
                        img3.setImageResource(R.drawable.circle)
                    }
                    4 -> {
                        img4.isEnabled = false
                        img4.setImageResource(R.drawable.circle)
                    }
                    5 -> {
                        img5.isEnabled = false
                        img5.setImageResource(R.drawable.circle)
                    }
                    6 -> {
                        img6.isEnabled = false
                        img6.setImageResource(R.drawable.circle)
                    }
                    7 -> {
                        img7.isEnabled = false
                        img7.setImageResource(R.drawable.circle)
                    }
                    8 -> {
                        img8.isEnabled = false
                        img8.setImageResource(R.drawable.circle)
                    }
                    else -> {
                        img9.isEnabled = false
                        img9.setImageResource(R.drawable.circle)
                    }
                }
            }
        }
    }
}