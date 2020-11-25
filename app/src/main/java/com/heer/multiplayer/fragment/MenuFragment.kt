package com.heer.multiplayer.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.heer.multiplayer.R
import com.heer.multiplayer.activity.MainActivity

class MenuFragment : Fragment() {

    private lateinit var btnSinglePlayer: Button
    private lateinit var btnMultiPlayer: Button
//    private lateinit var btnLeaderBoard: Button
    private lateinit var btnAbout: Button
    private lateinit var btnExit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        init(view)
        btnSinglePlayer.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fl,
                LevelsFragment()
            )?.commit()
        }
        btnMultiPlayer.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fl,
                OnlineOfflineFragment()
            )?.commit()
        }
  /*      btnLeaderBoard.setOnClickListener {
            Toast.makeText(activity, "Clicked LeaderBoard", Toast.LENGTH_SHORT).show()
        }
  */      btnAbout.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fl,
                AboutFragment()
            )?.commit()
        }
        btnExit.setOnClickListener {
            AlertDialog.Builder(activity as Context).setTitle("End Game")
                .setMessage("Do you want to exit the game?")
                .setPositiveButton("No", null)
                .setNegativeButton("Yes") { _, _ ->
                    ActivityCompat.finishAffinity(activity as Activity)
                }
                .create().show()
        }
        return view
    }

    private fun init(view: View) {
        btnSinglePlayer = view.findViewById(R.id.btnSinglePLayer)
        btnMultiPlayer = view.findViewById(R.id.btnMultiPlayer)
    //    btnLeaderBoard = view.findViewById(R.id.btnLeaderBoard)
        btnAbout = view.findViewById(R.id.btnAbout)
        btnExit = view.findViewById(R.id.btnExit)
        (activity as MainActivity).supportActionBar?.title = "Main Menu"
    }
}