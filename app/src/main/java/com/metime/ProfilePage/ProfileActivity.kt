package com.metime.ProfilePage

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.metime.Constants
import com.metime.Constants.Companion.profile
import com.metime.LoginActivity
import com.metime.LoginRegisterReset.MeProfile
import com.metime.R
import com.metime.setChallenge.ChallengeActivity
import com.socialtime.UsagePresenter
import com.socialtime.UsageStatsWrapper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.doAsync

class ProfileActivity : AppCompatActivity(), UsageContract.View{

    private lateinit var fireAuth: FirebaseAuth
    private lateinit var presenter: UsageContract.Presenter
    //private lateinit var adapter: UsageStatsAdapter
    private lateinit var firebaseDatabase : FirebaseDatabase
    private lateinit var firebaseStorage : FirebaseStorage
    private lateinit var profileName : TextView
    private lateinit var profileLoc : TextView
    private lateinit var profileChal : TextView
    private lateinit var profileFol : TextView
    private lateinit var setDur : TextView
    private lateinit var today : TextView
    private lateinit var week : TextView
    private lateinit var month : TextView
    private lateinit var profileCHart : PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        fireAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_profile)
        profileName = profile_name
        profileLoc = profile_location
        profileChal = profile_challenges
        profileFol = profile_followers

        doAsync {
            setupProfile()
            setupPic(profile_image)
        }

        profile_logout.setOnClickListener{
            fireAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        navigation.setOnClickListener {
            startActivity(Intent(this, ChallengeActivity::class.java))
        }

        //recyclerview.layoutManager = LinearLayoutManager(this)
        //adapter = UsageStatsAdapter()
        //recyclerview.adapter = adapter
        grant_permission_message!!.setOnClickListener { v -> openSettings() }

        setDur = Today
        today = Today
        week = Week
        month = Month
        presenter = UsagePresenter(this, this, 0)
        profileCHart = profile_chart

        Today.setOnClickListener(clickListener)
        Week.setOnClickListener (clickListener)
        Month.setOnClickListener (clickListener)
    }

    private val clickListener = View.OnClickListener {
        if (it != setDur) {
            setTab(it as TextView, setDur)
            when (it) {
                today -> presenter!!.retrieveUsageStats(-1)
                week -> presenter!!.retrieveUsageStats(-7)
                month -> presenter!!.retrieveUsageStats(-30)
            }
            setDur = it
        }
    }

    private fun openSettings() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    override fun onResume() {
        super.onResume()
        showProgressBar(true)
        when (setDur) {
            today -> presenter!!.retrieveUsageStats(-1)
            week -> presenter!!.retrieveUsageStats(-7)
            month -> presenter!!.retrieveUsageStats(-30)
        }
    }

    override fun onUsageStatsRetrieved(list: MutableList<UsageStatsWrapper>) {
        showProgressBar(false)
        grant_permission_message!!.visibility = View.GONE
        //adapter!!.setList(list)
        setupPie()
    }

    override fun onUserHasNoPermission() {
        showProgressBar(false)
        grant_permission_message!!.visibility = View.VISIBLE
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            progress_bar!!.visibility = View.VISIBLE
        } else {
            progress_bar!!.visibility = View.GONE
        }
    }

    fun setupPic(profilePic : ImageView) {
        if (Constants.image != null) {
            Picasso.get().load(Constants.image).fit().centerCrop(Gravity.CENTER).into(profilePic)
            return
        }
        firebaseStorage = FirebaseStorage.getInstance()
        val firebaseRef = firebaseStorage.getReference()
        firebaseRef.child("Profiles").child(fireAuth.uid!!).child("Profile Pic").downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).fit().centerCrop(Gravity.CENTER).into(profilePic)
        }
    }

    private fun setupProfile() {
        @SuppressLint("SetTextI18n")
        if (Constants.profile.name != "") {
            profileName.text = Constants.profile.name
            profileLoc.text = Constants.profile.city + ", " + profile!!.country
            profileChal.text = Constants.profile.challenges.toString()
            profileFol.text = Constants.profile.followers.toString()
            return
        }
        firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference("Users").child(fireAuth.uid!!)
        var profile : MeProfile?
        val eventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Cancelled Database call. <" + p0.code + ">", Toast.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                profile = p0.getValue(MeProfile::class.java)
                profileName.text = profile!!.name
                profileLoc.text = profile!!.city + ", " + profile!!.country
                profileChal.text = profile!!.challenges.toString()
                profileFol.text = profile!!.followers.toString()
                Constants.profile = profile as MeProfile
            }
        }
        databaseReference.addValueEventListener(eventListener)
    }

    fun setupPie() {
        val pieDataset = PieDataSet(Constants.pieData, "")
        pieDataset.setColors(
                resources.getColor(R.color.colorPrimaryDark),
                resources.getColor(R.color.color3),
                resources.getColor(R.color.colorPrimary),
                resources.getColor(R.color.colorAccent)
        )
        val pieData = PieData(pieDataset)

        profileCHart.data = pieData
        profileCHart.data.setValueFormatter(PercentFormatter())
        profileCHart.data.setValueTextSize(14f)
        profileCHart.data.setValueTextColor(Color.WHITE)
        profileCHart.description.text = ""
        profileCHart.description.textAlign = Paint.Align.LEFT
        profileCHart.description.textSize = 0f
        profileCHart.legend.position =  Legend.LegendPosition.ABOVE_CHART_LEFT
        profileCHart.setUsePercentValues(true)
        profileCHart.setDrawEntryLabels(false)
        profileCHart.animateY(2000)
        profileCHart.invalidate()
    }

    fun setTab(dur : TextView, pastDur : TextView) {
        if (dur != pastDur) {
            dur.setTextSize(14f)
            dur.background = (resources.getDrawable(R.drawable.profile_gradient))

            pastDur.setTextSize(12f)
            pastDur.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}
