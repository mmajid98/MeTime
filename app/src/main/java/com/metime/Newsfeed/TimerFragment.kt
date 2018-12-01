package com.metime.Newsfeed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.metime.ListChallenges.MyIntentService
import com.metime.R
import com.metime.setChallenge.Challenge
import kotlinx.android.synthetic.main.activity_set_challenge.*
import kotlinx.android.synthetic.main.timermenu.view.*
import java.util.ArrayList

class TimerFragment : android.support.v4.app.Fragment() {
    private lateinit var mview : View
    private lateinit var timer : CountDownTimer
    private lateinit var iconlist : MutableList<ImageView>
    private lateinit var fireAuth : FirebaseAuth
    private lateinit var firedb : FirebaseDatabase
    private var startedList = mutableListOf<Challenge>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mview = inflater.inflate(R.layout.timermenu, container, false)
        iconlist = mutableListOf(mview.d_icon1, mview.d_icon2, mview.d_icon3, mview.d_icon4)
        getProcessingTasks()
        return mview
    }

    private fun getProcessingTasks() {
        val processRef = firedb.getReference("Challenges").child(fireAuth.currentUser!!.uid).child("started").orderByChild("endTime")
        val eventListener = object : ChildEventListener {
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
                mview.drop_pledged.text = " Not running"
                for (i in iconlist) i.setImageDrawable(null)
                mview.drop_timer.setTextSize(0f)
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
        val serviceIntent = Intent(this.activity, MyIntentService::class.java)
        serviceIntent.putStringArrayListExtra("appNames", i.appNames as ArrayList<String>)
        serviceIntent.putExtra("start", i.startTime)
        serviceIntent.putExtra("end", i.endTime)
        serviceIntent.putExtra("money", i.money)
        serviceIntent.putExtra("charity", i.charity)
        serviceIntent.putExtra("key", i.key)
        activity!!.startService(serviceIntent)
    }

    private fun setupCurrent() {
        val i = startedList[0]
        mview.drop_pledged.text = "Pledged $" + i.money + " to " + i.charity
        mview.drop_timer.setTextSize(35f)
        for (s in 1..i.appNames.size) {
            iconlist[s - 1].setImageDrawable(activity!!.packageManager.getApplicationIcon(i.appNames[s-1]))
        }
        timer = object : CountDownTimer(startedList[0].endTime - System.currentTimeMillis(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var x = millisUntilFinished / 1000
                val hours = x / 3600
                x %= 3600
                val min = x / 60
                val sec = x % 60
                mview.drop_timer.text = String.format("%02d:%02d:%02d", hours, min, sec)
            }
            override fun onFinish() {
                mview.drop_timer.text = "00:00:00"
            }
        }.start()
    }
}