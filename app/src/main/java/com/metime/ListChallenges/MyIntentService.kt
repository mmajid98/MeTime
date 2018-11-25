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


class MyIntentService : IntentService("MyIntentService") {

    //private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var fireAuth : FirebaseAuth
    private lateinit var firedb : FirebaseDatabase
    private var dataSent = false
    private var key : String = ""
    init {
        setIntentRedelivery(true)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        usageStatsManager = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        fireAuth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()
        /*val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "ExampleApp:Wakelock")
        wakeLock!!.acquire()
        Log.d(TAG, "Wakelock acquired")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Example IntentService")
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.ic_android)
                    .build()

            startForeground(1, notification)
        }*/
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
                intent.getStringExtra("charity"),
                intent.getIntExtra("status", 0)
                )
        key = apps.key
        firedb.getReference("Challenges").child(fireAuth.currentUser!!.uid).child("started")
                .child(key).child("status").setValue(1)
        while (start < end) {
            Log.i("INTENTSERVICE", "entered loop")
            val usageStats = usageStatsManager.queryAndAggregateUsageStats(start, System.currentTimeMillis())
            val stats = ArrayList<UsageStats>()
            stats.addAll(usageStats.values)
            for (name in apps.appNames) {
                    if (name in usageStats.keys) {
                        if (usageStats.get(name)!!.totalTimeInForeground > 1000) {
                            apps.lost = 1
                            sendData(apps)
                            break
                        }
                    }
                if (dataSent) break
            }
            start = System.currentTimeMillis()
            SystemClock.sleep(60000)
        }
        if (dataSent) sendData(apps)

        /*for (i in 0..9) {
            Log.d(TAG, "$input - $i")
            SystemClock.sleep(1000)
        }*/
    }

    private fun sendData(apps: Challenge) {
        firedb.getReference("Challenges").child(fireAuth.currentUser!!.uid).child("started")
                .child(apps.key).removeValue()
        firedb.getReference("Challenges").child(FirebaseAuth.getInstance().currentUser!!.uid).child("processed")
                .child(key).setValue(apps)
        dataSent = true
    }

    override fun onDestroy() {
        if (dataSent == false) {
            firedb.getReference("Challenges").child(fireAuth.currentUser!!.uid)
                    .child(key).child("started/status").setValue(0)
        }
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        //wakeLock!!.release()
        //Log.d(TAG, "Wakelock released")
    }

    companion object {
        private val TAG = "ExampleIntentService"
    }
}