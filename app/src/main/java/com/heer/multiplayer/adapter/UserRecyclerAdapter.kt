package com.heer.multiplayer.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.heer.multiplayer.fragment.OnlineGameFragment
import com.heer.multiplayer.model.User
import com.heer.multiplayer.R

class UserRecyclerAdapter(
    val context: Context,
    private val userList: ArrayList<User>,
    private val myRef: DatabaseReference,
    private val userName: String
) : RecyclerView.Adapter<UserRecyclerAdapter.RecyclerViewHolder>() {

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

        when (user.reqType) {
            "" -> {
                holder.btnReqType.isEnabled = true
                holder.btnReqType.text = context.getString(R.string.send_request)
            }
            "request" -> {
                holder.btnReqType.isEnabled = false
                holder.btnReqType.text = context.getString(R.string.sent)
            }
            else -> {
                holder.btnReqType.isEnabled = true
                holder.btnReqType.text = context.getString(R.string.accept_request)
            }
        }

        holder.btnReqType.setOnClickListener {
            when (holder.btnReqType.text.toString()) {
                "Send" -> {
                    holder.btnReqType.isEnabled = false
                    holder.btnReqType.text = context.getString(R.string.sent)
                    myRef.child("users").child(userName).child("request")
                        .child(holder.txtUserName.text.toString())
                        .setValue("$userName:${holder.txtUserName.text}")
                    myRef.child("users").child(holder.txtUserName.text.toString()).child("accept")
                        .child(userName).setValue("$userName:${holder.txtUserName.text}")
                }
                "Accept" -> {
                    holder.btnReqType.isEnabled = false
                    holder.btnReqType.text = context.getString(R.string.done)
                    val player2 = userName
                    val player1 = myRef.child("users").child(userName).child("accept")
                        .child(holder.txtUserName.text.toString()).key.toString()
                    myRef.child("users").child(player1).child("game_state").setValue("1")
                    myRef.child("users").child(player2).child("game_state").setValue("1")
                    myRef.child("playing").child("$player1:$player2").child(player1)
                        .setValue("")
                    myRef.child("playing").child("$player1:$player2").child(player2)
                        .setValue("")
                    myRef.child("playing").child("$player1:$player2").child("exit")
                        .setValue("1")
                    myRef.child("playing").child("$player1:$player2")
                        .child("activePlayer").setValue(player1)
                }
            }
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("playing").exists()) {
                    val users = snapshot.child("playing").children
                    users.forEach { it ->
                        val players = it.key.toString()
                        val player1 = players.substring(0, players.indexOf(':'))
                        val player2 = players.substring(players.indexOf(":") + 1)
                        myRef.child("users").child(player1).child("accept").removeValue()
                        myRef.child("users").child(player2).child("accept").removeValue()
                        myRef.child("users").child(player1).child("request").removeValue()
                        myRef.child("users").child(player2).child("request").removeValue()

                        // also remove values from the other players who sent or received requests
                        // to be done
                        val activity: AppCompatActivity = context as AppCompatActivity
                        Log.d(
                            "Tag",
                            "player1: $player1 player2: $player2 curplayer userName: $userName"
                        )
                        if (player1 == userName || player2 == userName) {
                            val sharedPreferences = activity.getSharedPreferences(
                                activity.getString(R.string.shared_pref),
                                Context.MODE_PRIVATE
                            )
                            sharedPreferences.edit().putString("playing", "$player1:$player2")
                                .apply()

                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fl, OnlineGameFragment()).commitAllowingStateLoss()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}
