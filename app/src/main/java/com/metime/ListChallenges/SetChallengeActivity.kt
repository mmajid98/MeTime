package com.metime.ListChallenges

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.metime.R
import com.metime.setChallenge.Challenge
import java.util.*
import android.support.v4.content.ContextCompat
import android.content.Intent



class SetChallengeActivity : AppCompatActivity() {
    private lateinit var fireAuth : FirebaseAuth
    private lateinit var firedb : FirebaseDatabase
    private var startedList = mutableListOf<Challenge>()
    private val runningList = mutableListOf<Challenge>()
    private val completedList = mutableListOf<Challenge>()
    private val paidList = mutableListOf<Challenge>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()
        setContentView(R.layout.activity_set_challenge)
        getProcessingTasks()

        getRunningTasks()
        //getCompletedTasks()
    }

    private fun getRunningTasks() {
    }

    private fun getProcessingTasks() {
        val processRef = firedb.getReference("Challenges").child(fireAuth.currentUser!!.uid).child("started").orderByChild("endTime")
        val eventListener = object :  ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val i = p0.getValue(Challenge::class.java) as Challenge
                val serviceIntent = Intent(this@SetChallengeActivity, MyIntentService::class.java)
                serviceIntent.putStringArrayListExtra("appNames", i.appNames as ArrayList<String>)
                serviceIntent.putExtra("start", i.startTime)
                serviceIntent.putExtra("end", i.endTime)
                serviceIntent.putExtra("money", i.money)
                serviceIntent.putExtra("charity", i.charity)
                serviceIntent.putExtra("status", i.status)
                serviceIntent.putExtra("key", i.key)

                startService(serviceIntent)
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        processRef.addChildEventListener(eventListener)
    }
}
