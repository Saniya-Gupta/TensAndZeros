package com.heer.multiplayer.fragment

import android.app.AlertDialog
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
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.heer.multiplayer.R
import com.heer.multiplayer.activity.OnlineActivity

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
    private lateinit var txtGameStatus: TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var player1: String = ""
    private var player2: String = ""
    private var anotherPlayer = ""
    private lateinit var btnReplay: Button
    private var currentPlayer = ""
    private var movesCurrentPlayer = ""
    private var gameState = 1

    // 1 -> Game Active
    // 0 -> Game won or draw

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_online_game, container, false)
        init(view)
        enableClick()
        prepareGameSpace()
        btnReplay.setOnClickListener {
            myRef.child("playing").child("$player1:$player2")
                .child("exit").setValue("1")
            myRef.child("playing").child("$player1:$player2")
                .child("activePlayer").setValue(currentPlayer)
            btnReplay.isEnabled = false
        }
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
        txtGameStatus = view.findViewById(R.id.txtGameStatus)
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
        btnReplay = view.findViewById(R.id.btnReplay)
        btnReplay.isEnabled = false

        (activity as OnlineActivity).supportActionBar?.title = "Online Game"
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
        myRef.child("playing")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        if(activity != null) {
                            if (snapshot.child("$player1:$player2").exists()) {
                                movesCurrentPlayer = snapshot.child("$player1:$player2")
                                    .child(currentPlayer).value.toString()
                                val movesAnotherPlayer = snapshot.child("$player1:$player2")
                                    .child(anotherPlayer).value.toString()
                                checkWinner(movesCurrentPlayer, movesAnotherPlayer)
                                gameState = Integer.parseInt(
                                    snapshot.child("$player1:$player2")
                                        .child("exit").value as String
                                )
                                val activeStatePlayer = snapshot.child("$player1:$player2")
                                    .child("activePlayer").value.toString()

                                when {
                                    gameState == 0 -> {
                                        if (activeStatePlayer == "draw") {
                                            txtGameStatus.text = "Draw"
                                        } else {
                                            if(activeStatePlayer == currentPlayer) {
                                                txtGameStatus.text = "You have won"
                                            } else {
                                                txtGameStatus.text = "$activeStatePlayer has won"
                                            }
                                        }

                                        myRef.child("playing").child("$player1:$player2")
                                            .child(player1).setValue("")
                                        myRef.child("playing").child("$player1:$player2")
                                            .child(player2).setValue("")
                                        btnReplay.isEnabled = true

                                    }
                                    activeStatePlayer == currentPlayer -> {
                                        txtGameStatus.text = "Your turn"
                                        btnReplay.isEnabled = false
                                        disableClickCurrentPlayer(movesCurrentPlayer, currentPlayer)
                                        disableClickCurrentPlayer(movesAnotherPlayer, anotherPlayer)
                                    }
                                    else -> {
                                        txtGameStatus.text = "Another's turn"
                                        btnReplay.isEnabled = false
                                        disableClickCurrentPlayer(movesCurrentPlayer, currentPlayer)
                                        disableClickCurrentPlayer(movesAnotherPlayer, anotherPlayer)
                                        disableClick()
                                    }
                                }

                            } else {
                                activity?.finish()
                            }
                        }
                    } else {
                        activity?.finish()
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
            myRef.child("playing").child("$player1:$player2")
                .child(currentPlayer).setValue(movesCurrentPlayer)
            myRef.child("playing").child("$player1:$player2")
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
            myRef.child("playing").child("$player1:$player2")
                .child(currentPlayer).setValue(movesCurrentPlayer)
            myRef.child("playing").child("$player1:$player2")
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
            if (flag == 1) {
                myRef.child("playing").child("$player1:$player2")
                    .child("activePlayer").setValue(currentPlayer)
                myRef.child("playing").child("$player1:$player2").child("exit")
                    .setValue("0")
            } else if (movesAnotherPlayer.length + movesCurrentPlayer.length >= 9) {
                myRef.child("playing").child("$player1:$player2")
                    .child("activePlayer").setValue("draw")
                myRef.child("playing").child("$player1:$player2")
                    .child("exit")
                    .setValue("0")
            }
        }
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