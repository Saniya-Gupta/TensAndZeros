package com.heer.multiplayer.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.heer.multiplayer.R
import com.heer.multiplayer.activity.EasyActivity
import com.heer.multiplayer.activity.HardActivity
import com.heer.multiplayer.activity.MainActivity
import com.heer.multiplayer.activity.OnlineActivity

class LevelsFragment : Fragment() {

    private lateinit var btnEasy: Button
    //private lateinit var btnMedium: Button
    private lateinit var btnHard: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_levels, container, false)
        init(view)
        btnEasy.setOnClickListener {
            startActivity(Intent(activity, EasyActivity::class.java))
        }
  /*      btnMedium.setOnClickListener {
            Toast.makeText(activity, "Medium", Toast.LENGTH_SHORT).show()
        }
  */      btnHard.setOnClickListener {
            startActivity(Intent(activity, HardActivity::class.java))
        }
        return view
    }

    private fun init(view: View) {
        btnEasy = view.findViewById(R.id.btnEasy)
//        btnMedium = view.findViewById(R.id.btnMedium)
        btnHard = view.findViewById(R.id.btnHard)
        (activity as MainActivity).supportActionBar?.title = "Single Player Mode"
    }
}