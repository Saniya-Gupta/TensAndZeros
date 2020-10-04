package com.heer.multiplayer.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.heer.multiplayer.R
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
        init(view)
        getUsers()
        return view
    }

    private fun getUsers() {
        myRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.children
                user.forEach { name ->
                    if (userName != name.key.toString() && name.key.toString() != "playing") {
                        if(name.value == "")
                            listUser.add(User(name.key.toString(), name.value.toString()))
                        else if(name.hasChildren()) {
                            if(name.child("request").exists())
                                listUser.add(User(name.key.toString(), "request"))
                            else
                                listUser.add(User(name.key.toString(), "accept"))
                        }
                        Log.d("Tag", listUser.toString())
                        recyclerAdapter.notifyDataSetChanged()
                        progressLayout.visibility = View.GONE
                    }
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

        sharedPreferences = activity!!.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("userName", "").toString()

        progressLayout = view.findViewById(R.id.progressLayout)

        recyclerAdapter = UserRecyclerAdapter(activity as Context, listUser, myRef, userName)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = layoutManager

        progressLayout.visibility = View.VISIBLE
    }

}