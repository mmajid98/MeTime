package com.metime.ListChallenges

import android.os.SystemClock
import android.content.Intent
import android.app.IntentService
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.metime.setChallenge.Challenge
import java.util.ArrayList
import android.app.NotificationManager
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat
import com.metime.R


class MyIntentService : IntentService("MyIntentService") {

    private var mNotificationManager: NotificationManager? = null
    val NOTIFICATION_ID = 1
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var fireAuth : FirebaseAuth
    private lateinit var firedb : FirebaseDatabase
    private var dataSent = false
    private var key : String = ""
    init {
        setIntentRedelivery(false)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        usageStatsManager = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        fireAuth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent")

        var start : Long = intent!!.getLongExtra("start", 0)
        val end : Long = intent.getLongExtra("end", 0)
        key = intent.getStringExtra("key")
        val apps = Challenge(key,
                intent.getIntExtra("money", 0),
                intent.getLongExtra("start", 0),
                intent.getLongExtra("end", 0),
                intent.getStringArrayListExtra("appNames"),
                intent.getStringExtra("charity")
                )
        key = apps.key
        if (end > System.currentTimeMillis()) {
            val startTime = start
            while (start < end) {
                Log.i(TAG, "entered loop")
                val usageStats = usageStatsManager.queryAndAggregateUsageStats(startTime, System.currentTimeMillis())
                val stats = ArrayList<UsageStats>()
                stats.addAll(usageStats.values)
                for (name in apps.appNames) {
                    if (name in usageStats.keys) {
                        if (usageStats.get(name)!!.lastTimeUsed > startTime) {
                            Log.i(TAG, "CHALLENGE FAILED")
                            apps.lost = 1
                            sendData(apps)
                            break
                        }
                    }
                }
                if (dataSent) break
                SystemClock.sleep(30000)
                start = System.currentTimeMillis()
            }
        }
        else {
            Log.i(TAG, "entered single")
            val usageStats = usageStatsManager.queryAndAggregateUsageStats(start, end)
            val stats = ArrayList<UsageStats>()
            stats.addAll(usageStats.values)
            for (name in apps.appNames) {
                if (name in usageStats.keys) {
                    if (usageStats.get(name)!!.lastTimeUsed > start && usageStats.get(name)!!.lastTimeUsed < end) {
                        Log.i(TAG, "CHALLENGE FAILED")
                        apps.lost = 1
                        sendData(apps)
                        break
                    }
                }
            }
        }
        if (!dataSent) sendData(apps)
    }

    private fun sendData(apps: Challenge) {
        firedb.getReference("Challenges").child(fireAuth.currentUser!!.uid).child("started")
                .child(apps.key).removeValue()
        firedb.getReference("Challenges").child(FirebaseAuth.getInstance().currentUser!!.uid).child("processed")
                .child(key).setValue(apps)
        dataSent = true
        sendNotification(apps.lost)
        stopSelf()
    }

    private fun sendNotification(lost : Int) {
        mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val contentIntent = PendingIntent.getActivity(this, 0,
                Intent(this, SetChallengeActivity::class.java), 0)

        val mBuilder = when(lost) {

            0 -> {
                NotificationCompat.Builder(this)
                        .setContentTitle("Challenge Completed")
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("Hurray!"))
                        .setContentText("Hurray! You have won the challenge!")
                        .setSmallIcon(R.drawable.symbol)
                        .setVibrate(longArrayOf(500, 1000))
            }
            else -> {
                NotificationCompat.Builder(this)
                        .setContentTitle("Challenge Completed")
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("Thank You!"))
                        .setContentText("Thank You for your Donation!")
                        .setSmallIcon(R.drawable.symbol)
                        .setVibrate(longArrayOf(500, 1000))
            }
        }
                    mBuilder . setContentIntent (contentIntent)
        mNotificationManager!!.notify(NOTIFICATION_ID, mBuilder.build())
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    companion object {
        private val TAG = "INTENTSERVICE"
    }
}