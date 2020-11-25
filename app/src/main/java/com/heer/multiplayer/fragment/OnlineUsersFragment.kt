package com.heer.multiplayer.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.heer.multiplayer.R
import com.heer.multiplayer.activity.MainActivity
import com.heer.multiplayer.activity.OnlineActivity
import com.heer.multiplayer.adapter.UserRecyclerAdapter
import com.heer.multiplayer.model.User


class OnlineUsersFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private val listUser = arrayListOf<User>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: UserRecyclerAdapter
    private lateinit var progressLayout: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_online_users, container, false)
        activity?.title = "Users"
        init(view)
        getUsers()
        return view
    }

    private fun getUsers() {

        myRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.children
                if (snapshot.child(userName).child("request")
                        .exists()
                ) // check if main user has sent any requests
                {
                    val requestList = snapshot.child(userName).child("request").children
                    requestList.forEach {
                        if (snapshot.child(it.key.toString()).child("game_state").value == "0") {
                            if (!listUser.contains(User(it.key.toString(), "request"))) {
                                listUser.add(User(it.key.toString(), "request"))
                            }
                        }
                    }
                } else if (snapshot.child(userName).child("accept")
                        .exists()
                ) // check if main user has received any requests
                {
                    val acceptList = snapshot.child(userName).child("accept").children
                    acceptList.forEach {
                        if (snapshot.child(it.key.toString()).child("game_state").value == "0") {
                            if (!listUser.contains(User(it.key.toString(), "accept"))) {
                                listUser.add(User(it.key.toString(), "accept"))
                            }
                        }
                    }
                }
                user.forEach { name ->
                    // check if user is in inactive game-play state
                    if (name.child("game_state").value == "0") {
                        val player = name.key.toString()
                        if (userName != player) {
                            // if this user has neither sent or received any requests from main user
                            if (!(listUser.contains(User(player, "request")) ||
                                        listUser.contains(User(player, "accept")) ||
                                        listUser.contains(User(player, "")))
                            )
                                listUser.add(User(name.key.toString(), ""))
                        }
                    }
                    recyclerAdapter.notifyDataSetChanged()
                    progressLayout.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun init(view: View) {
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        recyclerView = view.findViewById(R.id.recycler)
        layoutManager = LinearLayoutManager(activity)

        sharedPreferences =
            activity!!.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("userName", "").toString()

        progressLayout = view.findViewById(R.id.progressLayout)

        recyclerAdapter = UserRecyclerAdapter(activity as Context, listUser, myRef, userName)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = layoutManager

        progressLayout.visibility = View.VISIBLE
        (activity as OnlineActivity).supportActionBar?.title = "User List"
    }

}