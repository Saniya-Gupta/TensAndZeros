package com.heer.multiplayer

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


@Suppress("UNCHECKED_CAST")
class OnlineLoginActivity : AppCompatActivity() {

    private lateinit var lvRegisteredUsers: ListView
    private var registeredUsers = arrayListOf<Any>()
    private lateinit var registeredAdapter: ArrayAdapter<Any>

    // Add firebase analytics to your project
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    // Add authentication
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private lateinit var lvRequestedUsers: ListView
    private var requestedUsers = arrayListOf<Any>()
    private lateinit var requestUsersAdapter: ArrayAdapter<Any>

    private lateinit var txtUserId: TextView
    private lateinit var txtSendRequest: TextView
    private lateinit var txtAcceptRequest: TextView

    // Get user id and email
    private lateinit var loginUserId: String
    private lateinit var loginUserName: String
    private lateinit var loginUID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_login)

        txtSendRequest = findViewById(R.id.txtSendRequest)
        txtAcceptRequest = findViewById(R.id.txtAcceptRequest)
        txtUserId = findViewById(R.id.txtUserId)

        txtSendRequest.text = getString(R.string.please_wait)
        txtAcceptRequest.text = getString(R.string.please_wait)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()

        // Write to your realtime database
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        lvRegisteredUsers = findViewById(R.id.lvRegisteredUsers)
        registeredAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            registeredUsers
        )
        lvRegisteredUsers.adapter = registeredAdapter

        lvRequestedUsers = findViewById(R.id.lvRequestedUsers)
        requestUsersAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            requestedUsers
        )
        lvRequestedUsers.adapter = requestUsersAdapter

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = mAuth.currentUser
        if(currentUser != null) {
            loginUID = currentUser.uid
            loginUserId = currentUser.email.toString()
            txtUserId.text = loginUserId
            loginUserName = encodeUserEmail(loginUserId)
            Log.d("TAG", "User Encoded Email: $loginUserName")
            Log.d("TAG", "User login UID: $loginUID")
            myRef.child("users").child(loginUserName).child("request").setValue(loginUID)
            requestUsersAdapter.clear()
            acceptIncomingRequests()
        } else {
            Log.d("TAG", "Failed, Signed out to be registered")
            joinOnlineGame()
        }
        //updateUI(currentUser)

        myRef.root.child("users").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                updateLoginUsers(p0)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        lvRegisteredUsers.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val txtView: TextView = view as TextView
                val requestToUser: String = txtView.text.toString()
                confirmRequest(requestToUser, "To")
            }
        })

        lvRequestedUsers.setOnItemClickListener(object: AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val txtView: TextView = view as TextView
                val requestFromUser: String = txtView.text.toString()
                confirmRequest(requestFromUser, "From")
            }

        })
    }

    private fun confirmRequest(otherPlayer: String, requestToFrom: String) {
        val newOtherPlayer = otherPlayer.substring(1, otherPlayer.length - 1)
        val d = Log.d("TAG", "Other player: ${otherPlayer.substring(1, otherPlayer.length - 1)}")
        val b = AlertDialog.Builder(this@OnlineLoginActivity)
        val inflater:LayoutInflater = this.layoutInflater
        val view = inflater.inflate(R.layout.connect_player_dialog, null)
        b.setView(view).setTitle("Start").setMessage("Connect with $newOtherPlayer")
            .setPositiveButton("Yes", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    myRef.child("users").child(newOtherPlayer).child("request").push().setValue(loginUserId)
                    if(requestToFrom.equals("From",true)) {
                        startGame("$newOtherPlayer:$loginUserName", newOtherPlayer, "From")
                    } else {
                        startGame("$loginUserName:$newOtherPlayer", newOtherPlayer, "To")
                    }
                }
            }).setNegativeButton("Back", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            }).show()
    }

    private fun startGame(
        playerGameId: String,
        otherPlayer: String,
        requestToFrom: String
    ) {
        myRef.child("playing").child(playerGameId).removeValue()
        val i = Intent(this@OnlineLoginActivity, OnlineGameActivity::class.java)
        i.putExtra("playerSession", playerGameId)
        i.putExtra("userName", loginUserName)
        i.putExtra("otherPlayer", otherPlayer)
        i.putExtra("loginUid", loginUID)
        i.putExtra("requestType", requestToFrom)
        startActivity(i)
        finish()
    }

    private fun updateLoginUsers(dataSnapshot: DataSnapshot) {
        var key: String = ""
        val set = HashSet<String>()
        var i = dataSnapshot.children.iterator()
        while(i.hasNext()) {
            key = i.next().key.toString()
            if(!key.equals(loginUserName, true)) {
                set.add(key);
            }
        }

        registeredAdapter.clear()
        registeredAdapter.add(set)
        registeredAdapter.notifyDataSetChanged()

        txtSendRequest.text = "Send request to"
        txtAcceptRequest.text = "Accept request from"
    }

    private fun joinOnlineGame() {
        val b = AlertDialog.Builder(this)
        val inflater:LayoutInflater = this.layoutInflater
        val view = inflater.inflate(R.layout.login_dialog, null)
        b.setView(view)

        val etEmail: EditText = view.findViewById(R.id.etEmail)
        val etPassword: EditText = view.findViewById(R.id.etPassword)

        b.setTitle("Please register")
            .setMessage("Enter you email and password")
            .setPositiveButton("Register", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    registerUser(etEmail.text.toString(), etPassword.text.toString())
                }
            }).setNegativeButton("Back", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    startActivity(Intent(this@OnlineLoginActivity, MenuActivity::class.java))
                    finish()
                }
            }).show()
    }

    private fun acceptIncomingRequests() {
        myRef.child("users").child(loginUserName).child("request")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        Log.d("TAG","${p0.value}")
                        val map: HashMap<String, Any> = p0.value as HashMap<String, Any>
                        var value: String = ""
                        for(key in map.keys) {
                            value = key
                            requestUsersAdapter.add(encodeUserEmail(value))
                            requestUsersAdapter.notifyDataSetChanged()
                            myRef.child("users").child(loginUserName).child("request").setValue(loginUserId)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun encodeUserEmail(email: String): String {
        return email.replace(".", ",")
        /*
        * As your error said, Firebase Database paths must not contain
        * '.', '#', '$', '[', or ']'.
        * This means that Firebase does not allow you use in the key symbols those symbols.
        * Because of that, you need to encode the email address like this:
        name@email.com -> name@email,com * */
        // Decode: return userEmail.replace(",", ".");
    }

    fun registerUser(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    //val user = mAuth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@OnlineLoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }
    }
}
