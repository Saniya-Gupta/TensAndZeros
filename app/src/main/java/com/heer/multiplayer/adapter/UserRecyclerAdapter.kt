package com.heer.multiplayer.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.heer.multiplayer.R
import com.heer.multiplayer.fragment.OnlineGameFragment
import com.heer.multiplayer.model.User

class UserRecyclerAdapter(
    val context: Context,
    private val userList: List<User>, val myRef: DatabaseReference, val userName: String
) : RecyclerView.Adapter<UserRecyclerAdapter.RecyclerViewHolder>() {

    private var player1 = ""
    private var player2 = ""
    lateinit var sharedPreferences: SharedPreferences

    class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.txtUserName)
        val btnReqType: Button = view.findViewById(R.id.btnReqType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_row_users, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUserName.text = user.name

        val acceptList = arrayListOf<String>()
        val requestList = arrayListOf<String>()

        myRef.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("playing").hasChildren()) {
                    if (snapshot.child("playing").child("$player1:$player2").exists()) {
                        val activity: AppCompatActivity = context as AppCompatActivity
                        sharedPreferences = activity.getSharedPreferences(
                            activity.getString(R.string.shared_pref),
                            Context.MODE_PRIVATE
                        )
                        sharedPreferences.edit().putString("playing", "$player1:$player2").apply()
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fl, OnlineGameFragment()).commit()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        myRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(userName).hasChildren()) {
                    if (snapshot.child("accept").exists()) {
                        Log.d("Tag", "Accept list for $userName exists")
                        snapshot.child("accept").children.forEach { name ->
                            acceptList.add(name.key.toString())
                            Log.d("Tag", acceptList.toString())
                            if (acceptList.contains(holder.txtUserName.text.toString())) {
                                holder.btnReqType.isEnabled = false
                                holder.btnReqType.text = "Sent"
                                notifyDataSetChanged()
                            }
                        }
                    } else if (snapshot.child("request").exists()) {
                        Log.d("Tag", "Request list for $userName exists")
                        snapshot.child("request").children.forEach { name ->
                            requestList.add(name.key.toString())
                            Log.d("Tag", requestList.toString())
                            if (requestList.contains(holder.txtUserName.text.toString())) {
                                holder.btnReqType.isEnabled = true
                                holder.btnReqType.text = "Accept"
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        if (user.reqType == "") {
            holder.btnReqType.isEnabled = true
            holder.btnReqType.text = "Send"
        } else if (user.reqType == "request") {
            holder.btnReqType.isEnabled = true
            holder.btnReqType.text = "Accept"
        } else {
            holder.btnReqType.isEnabled = false
            holder.btnReqType.text = "Sent"
        }

        holder.btnReqType.setOnClickListener {
            when (holder.btnReqType.text.toString()) {
                "Send" -> {
                    holder.btnReqType.isEnabled = false
                    holder.btnReqType.text = "Sent"
                    player1 = userName
                    player2 = holder.txtUserName.text.toString()
                    myRef.child("users").child(userName).child("request")
                        .child(holder.txtUserName.text.toString())
                        .setValue("$userName:${holder.txtUserName.text}")
                    myRef.child("users").child(holder.txtUserName.text.toString()).child("accept")
                        .child(userName).setValue("$userName:${holder.txtUserName.text}")
                }
                "Accept" -> {
                    holder.btnReqType.isEnabled = false
                    holder.btnReqType.text = "Done"
                    player2 = userName
                    player1 = myRef.child("users").child(userName).child("accept")
                        .child(holder.txtUserName.text.toString()).key.toString()
                    Log.d("Tag", "player1: $player1 player2: $player2")
                    myRef.child("users").child(player1).removeValue()
                    myRef.child("users").child(player2).removeValue()
                    myRef.child("users").child("playing").child("$player1:$player2").child(player1)
                        .setValue("")
                    myRef.child("users").child("playing").child("$player1:$player2").child(player2)
                        .setValue("")
                    myRef.child("users").child("playing").child("$player1:$player2").child("exit")
                        .setValue("1")
                    myRef.child("users").child("playing").child("$player1:$player2")
                        .child("activePlayer").setValue(player1)
                }
            }
        }
    }
}
