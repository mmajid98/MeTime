package com.metime.ListChallenges

import android.annotation.SuppressLint
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
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_set_challenge.*
import android.os.CountDownTimer
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.metime.setChallenge.SetViewHolder
import kotlinx.android.synthetic.main.completed_challenges.*
import kotlinx.android.synthetic.main.completed_challenges.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import kotlin.concurrent.timer


class SetChallengeActivity : AppCompatActivity() {
    private lateinit var timer : CountDownTimer
    private lateinit var iconlist : List<ImageView>
    val sdf = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    private lateinit var fireAuth : FirebaseAuth
    private lateinit var firedb : FirebaseDatabase
    private var startedList = mutableListOf<Challenge>()
    private val processedList = mutableListOf<Challenge>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_set_challenge)
        iconlist = listOf(icon1, icon2, icon3, icon4)
        getProcessingTasks()
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
                if (model.lost == 1) {
                    holder.itemView.setlayout.background = holder.itemView.resources.getDrawable(R.drawable.purple_gradient)
                    holder.itemView.settitle.text = "Thank You!"
                    holder.itemView.setupdate.text = "For your generous Donation to " + model.charity
                    holder.itemView.settime.text = sdf.format(model.endTime)
                }
                else {
                    holder.itemView.setlayout.background = holder.itemView.resources.getDrawable(R.drawable.profile_gradient)
                    holder.itemView.settitle.text = "Hurray!"
                    holder.itemView.setupdate.text = "You have successfully completed the Challenge!"
                    holder.itemView.settime.text = sdf.format(model.endTime)
                }
            }

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SetViewHolder {
                return SetViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.completed_challenges, p0, false))
            }
        }
        setrecyclerview.adapter = firebaseRecAdapter
    }

    private fun processPayment(i: Challenge) {

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
                startedList.add(i)
                setupCurrentTask()
                setupCurrent()
            }
            override fun onChildRemoved(p0: DataSnapshot) {
                startedList.removeAt(0)
                pledged.text = " Not running"
                for (i in iconlist) i.setImageDrawable(null)
                Timer.setTextSize(0f)
                if (startedList.size > 0){
                    setupCurrent()
                    setupCurrentTask()
                }
            }
        }
        processRef.addChildEventListener(eventListener)
    }

    private fun setupCurrentTask() {
        val i = startedList[0]
        val serviceIntent = Intent(this@SetChallengeActivity, MyIntentService::class.java)
        serviceIntent.putStringArrayListExtra("appNames", i.appNames as ArrayList<String>)
        serviceIntent.putExtra("start", i.startTime)
        serviceIntent.putExtra("end", i.endTime)
        serviceIntent.putExtra("money", i.money)
        serviceIntent.putExtra("charity", i.charity)
        serviceIntent.putExtra("key", i.key)
        startService(serviceIntent)
    }

    private fun setupCurrent() {
        val i = startedList[0]
        pledged.text = "Pledged $" + i.money + " to " + i.charity
        Timer.setTextSize(50f)
        for (s in 1..i.appNames.size) {
            iconlist[s - 1].setImageDrawable(packageManager.getApplicationIcon(i.appNames[s-1]))
        }
        timer = object : CountDownTimer(startedList[0].endTime - System.currentTimeMillis(), 1000) {
            val sfd = SimpleDateFormat("mm:ss")
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var x = millisUntilFinished / 1000
                val hours = x / 3600
                x %= 3600
                val min = x / 60
                val sec = x % 60
                Timer.text = String.format("%02d:%02d:%02d", hours, min, sec)
            }
            override fun onFinish() {
                Timer.text = "00:00:00"
            }
        }.start()
    }
}
