package com.metime.ListChallenges

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.metime.R
import com.metime.setChallenge.Challenge
import android.content.Intent
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_set_challenge.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.metime.Newsfeed.NewsfeedActivity
import com.metime.ProfilePage.ProfileActivity
import com.metime.setChallenge.SetViewHolder
import kotlinx.android.synthetic.main.completed_challenges.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat


class SetChallengeActivity : AppCompatActivity() {

    val sdf = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    private lateinit var fireAuth : FirebaseAuth
    private lateinit var firedb : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_set_challenge)
    }

    override fun onStart() {
        super.onStart()
        setrecyclerview.layoutManager = LinearLayoutManager(this)
        val processRef = firedb.getReference("Challenges").child(fireAuth.currentUser!!.uid).child("processed").orderByChild("endTime")
        val options = FirebaseRecyclerOptions.Builder<Challenge>()
                .setQuery(processRef, Challenge::class.java)
                .setLifecycleOwner(this)
                .build()

        val firebaseRecAdapter = object : FirebaseRecyclerAdapter<Challenge, RecyclerView.ViewHolder>(options) {

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Challenge) {
                holder.itemView.setmoney.text = "$"+model.money
                holder.itemView.settime.text = NewsfeedActivity.setDateFormat(model.endTime)
                if (model.lost == 1) {
                    holder.itemView.setlayout.background = holder.itemView.resources.getDrawable(R.drawable.purple_gradient)
                    holder.itemView.settitle.text = "Thank You!"
                    holder.itemView.setupdate.text = "For your generous Donation to " + model.charity
                }
                else {
                    holder.itemView.setlayout.background = holder.itemView.resources.getDrawable(R.drawable.profile_gradient)
                    holder.itemView.settitle.text = "Hurray!"
                    holder.itemView.setupdate.text = "You have successfully completed the Challenge!"
                }
            }

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SetViewHolder {
                return SetViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.completed_challenges, p0, false))
            }
        }
        setrecyclerview.adapter = firebaseRecAdapter
        challengeToNav.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun processPayment(i: Challenge) {

    }
}
