package com.heer.multiplayer.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.heer.multiplayer.R
import java.util.*

class EasyActivity : AppCompatActivity() {

    private var gameState = 0 // 0: Active, 1: X Wins, 2: Y wins 4: Draw
    private var boardState = Array(3) { Array(3) { 0 } } // Stores current status of board
    private var moves = 0 // No. of moves played

    private lateinit var img00: ImageView
    private lateinit var img01: ImageView
    private lateinit var img02: ImageView
    private lateinit var img10: ImageView
    private lateinit var img11: ImageView
    private lateinit var img12: ImageView
    private lateinit var img20: ImageView
    private lateinit var img21: ImageView
    private lateinit var img22: ImageView
    private lateinit var txtPlayerStatus: TextView
    private lateinit var btnReplay: Button
    private lateinit var txtCountTimer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy)
        title = "Easy Game"
        init()
        txtPlayerStatus.text = getString(R.string.player_turn)
        btnReplay.setOnClickListener {
            resetGame()
        }
    }

    private fun init() {
        img00 = findViewById(R.id.img00)
        img01 = findViewById(R.id.img01)
        img02 = findViewById(R.id.img02)
        img10 = findViewById(R.id.img10)
        img11 = findViewById(R.id.img11)
        img12 = findViewById(R.id.img12)
        img20 = findViewById(R.id.img20)
        img21 = findViewById(R.id.img21)
        img22 = findViewById(R.id.img22)
        txtPlayerStatus = findViewById(R.id.txtPlayerStatus)
        btnReplay = findViewById(R.id.btnReplay)
        txtCountTimer = findViewById(R.id.txtCountTimer)
    }

    fun onGameClick(view: View) {
        val blockSelected: ImageView = view as ImageView
        when (checkWinnerIfAny()) {
            0 -> {
                moves += 1
                when (blockSelected.id) {
                    R.id.img00 -> fillBlock(0, 0, 1)
                    R.id.img01 -> fillBlock(0, 1, 1)
                    R.id.img02 -> fillBlock(0, 2, 1)
                    R.id.img10 -> fillBlock(1, 0, 1)
                    R.id.img11 -> fillBlock(1, 1, 1)
                    R.id.img12 -> fillBlock(1, 2, 1)
                    R.id.img20 -> fillBlock(2, 0, 1)
                    R.id.img21 -> fillBlock(2, 1, 1)
                    R.id.img22 -> fillBlock(2, 2, 1)
                }
                txtPlayerStatus.text = getString(R.string.comp_turn)
                autoPlayUsingRandom()
            }
            1 -> displayResult("You have won")
            4 -> displayResult("Game tied")
            else -> displayResult("Computer won")
        }
    }

    private fun displayResult(msg: String) {
        txtPlayerStatus.text = msg
/*
        AlertDialog.Builder(this).setTitle("New Game?")
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                resetGame()
            }
            .setNegativeButton("No") { _, _ ->
                finish()
            }
            .setIcon(android.R.drawable.ic_dialog_info)
            .create()
            .show()
*/
    }

    private fun autoPlayUsingRandom() // easy level since computer hardly ever wins
    {
        when (checkWinnerIfAny()) {
            0 -> {
                moves += 1
                val r = Random()
                var row = r.nextInt(3)
                var column = r.nextInt(3)

                while (boardState[row][column] != 0) {
                    row = r.nextInt(3)
                    column = r.nextInt(3)
                }
                fillBlock(row, column, 2)
                txtPlayerStatus.text = getString(R.string.player_turn)
            }
            1 -> displayResult("You have won")
            4 -> displayResult("Game tied")
            else -> displayResult("Computer won")
        }
    }

/*
    private fun showTimer(msg: String) {
        var counter = 2
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                txtCountTimer.text = java.lang.String.valueOf(counter)
                counter--
            }
            override fun onFinish() {
                txtPlayerStatus.text = msg
            }
        }.start()
    }
*/

    private fun fillBlock(row: Int, column: Int, player: Int) {
        boardState[row][column] = player
        when (row) {
            0 -> when (column) {
                0 -> {
                    img00.isEnabled = false
                    if (player == 1)
                        img00.setImageResource(R.drawable.cross)
                    else
                        img00.setImageResource(R.drawable.circle)

                }
                1 -> {
                    img01.isEnabled = false
                    if (player == 1)
                        img01.setImageResource(R.drawable.cross)
                    else
                        img01.setImageResource(R.drawable.circle)
                }
                else -> {
                    img02.isEnabled = false
                    if (player == 1)
                        img02.setImageResource(R.drawable.cross)
                    else
                        img02.setImageResource(R.drawable.circle)
                }
            }

            1 -> when (column) {
                0 -> {
                    img10.isEnabled = false
                    if (player == 1)
                        img10.setImageResource(R.drawable.cross)
                    else
                        img10.setImageResource(R.drawable.circle)
                }
                1 -> {
                    img11.isEnabled = false
                    if (player == 1)
                        img11.setImageResource(R.drawable.cross)
                    else
                        img11.setImageResource(R.drawable.circle)
                }
                else -> {
                    img12.isEnabled = false
                    if (player == 1)
                        img12.setImageResource(R.drawable.cross)
                    else
                        img12.setImageResource(R.drawable.circle)
                }
            }

            else -> when (column) {
                0 -> {
                    img20.isEnabled = false
                    if (player == 1)
                        img20.setImageResource(R.drawable.cross)
                    else
                        img20.setImageResource(R.drawable.circle)
                }
                1 -> {
                    img21.isEnabled = false
                    if (player == 1)
                        img21.setImageResource(R.drawable.cross)
                    else
                        img21.setImageResource(R.drawable.circle)
                }
                else -> {
                    img22.isEnabled = false
                    if (player == 1)
                        img22.setImageResource(R.drawable.cross)
                    else
                        img22.setImageResource(R.drawable.circle)
                }
            }
        }

    }

    private fun resetGame() {
        txtPlayerStatus.text = getString(R.string.player_turn)
        moves = 0
        gameState = 0

        for (i in 0..2)
            for (j in 0..2)
                boardState[i][j] = 0

        emptyBoard()
    }

    private fun emptyBoard() {

        img00.setImageResource(0)
        img01.setImageResource(0)
        img02.setImageResource(0)
        img10.setImageResource(0)
        img11.setImageResource(0)
        img12.setImageResource(0)
        img20.setImageResource(0)
        img21.setImageResource(0)
        img22.setImageResource(0)

        img00.isEnabled = true
        img01.isEnabled = true
        img02.isEnabled = true
        img20.isEnabled = true
        img21.isEnabled = true
        img22.isEnabled = true
        img10.isEnabled = true
        img11.isEnabled = true
        img12.isEnabled = true

    }

    private fun checkWinnerIfAny(): Int {
        if (boardState[0][0] != 0 && boardState[0][0] == boardState[0][1] && boardState[0][1] == boardState[0][2]) // Row 1
            return boardState[0][0]
        else if (boardState[1][0] != 0 && boardState[1][0] == boardState[1][1] && boardState[1][1] == boardState[1][2]) // Row 2
            return boardState[1][0]
        else if (boardState[2][0] != 0 && boardState[2][0] == boardState[2][1] && boardState[2][1] == boardState[2][2]) // Row 3
            return boardState[2][0]
        else if (boardState[0][0] != 0 && boardState[0][0] == boardState[1][0] && boardState[1][0] == boardState[2][0]) // Column 1
            return boardState[0][0]
        else if (boardState[0][1] != 0 && boardState[0][1] == boardState[1][1] && boardState[1][1] == boardState[2][1]) // Column 2
            return boardState[0][1]
        else if (boardState[0][2] != 0 && boardState[0][2] == boardState[1][2] && boardState[1][2] == boardState[2][2]) // Column 3
            return boardState[0][2]
        else if (boardState[0][0] != 0 && boardState[0][0] == boardState[1][1] && boardState[1][1] == boardState[2][2]) // Left to right
            return boardState[0][0]
        else if (boardState[0][2] != 0 && boardState[0][2] == boardState[1][1] && boardState[1][1] == boardState[2][0]) // Right to left
            return boardState[0][2]
        else if (moves == 9) // Draw
            return 4
        return 0 // Game Active
    }
}

