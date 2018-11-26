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
import com.metime.ListChallenges.SetChallengeActivity
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
    private lateinit var firebaseDatabase : FirebaseDatabase
    private lateinit var setDur : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        fireAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_profile)
        if (Constants.image != null) {
            Picasso.get().load(Constants.image).fit().centerCrop(Gravity.CENTER).into(profile_image)
        } else {
            doAsync { Constants.setupPic(profile_image) }
        }
        if (Constants.profile != null) {
            profile_name.text = Constants.profile?.name
            profile_location.text = Constants.profile?.city + ", " + profile?.country
            profile_challenges.text = Constants.profile?.challenges.toString()
            profile_followers.text = Constants.profile?.followers.toString()
            return
        } else doAsync { setupProfile() }

    }

    private val clickListener = View.OnClickListener {
        if (it != setDur) {
            setTab(it as TextView, setDur)
            when (it) {
                Today -> presenter.retrieveUsageStats(-1)
                Week -> presenter.retrieveUsageStats(-7)
                Month -> presenter.retrieveUsageStats(-30)
            }
            setDur = it
        }
    }

    private fun openSettings() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    override fun onStart() {
        super.onStart()
        setDur = Today
        presenter = UsagePresenter(this, this, 0)
        profile_challenges.text = Constants.profile?.challenges.toString()
        profile_followers.text = Constants.profile?.followers.toString()

        profile_logout.setOnClickListener{
            fireAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        navigation.setOnClickListener {
            startActivity(Intent(this, ChallengeActivity::class.java))
        }
        goto_but.setOnClickListener{
            startActivity(Intent(this, SetChallengeActivity::class.java))
        }
        grant_permission_message!!.setOnClickListener { v -> openSettings() }
        Today.setOnClickListener(clickListener)
        Week.setOnClickListener (clickListener)
        Month.setOnClickListener (clickListener)
    }

    override fun onResume() {
        super.onResume()
        showProgressBar(true)
        when (setDur) {
            Today -> presenter!!.retrieveUsageStats(-1)
            Week -> presenter!!.retrieveUsageStats(-7)
            Month -> presenter!!.retrieveUsageStats(-30)
        }
    }

    override fun onUsageStatsRetrieved(list: MutableList<UsageStatsWrapper>) {
        showProgressBar(false)
        grant_permission_message!!.visibility = View.GONE
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

    private fun setupProfile() {
        @SuppressLint("SetTextI18n")

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
                profile_name.text = profile!!.name
                profile_location.text = profile!!.city + ", " + profile!!.country
                profile_challenges.text = profile!!.challenges.toString()
                profile_followers.text = profile!!.followers.toString()
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

        profile_chart.data = pieData
        profile_chart.data.setValueFormatter(PercentFormatter())
        profile_chart.data.setValueTextSize(14f)
        profile_chart.data.setValueTextColor(Color.WHITE)
        profile_chart.description.text = ""
        profile_chart.description.textAlign = Paint.Align.LEFT
        profile_chart.description.textSize = 0f
        profile_chart.legend.position =  Legend.LegendPosition.ABOVE_CHART_LEFT
        profile_chart.setUsePercentValues(true)
        profile_chart.setDrawEntryLabels(false)
        profile_chart.animateY(2000)
        profile_chart.invalidate()
        profile_chart.legend.textSize = 12f
    }

    private fun setTab(dur : TextView, pastDur : TextView) {
        if (dur != pastDur) {
            dur.setTextSize(16f)
            dur.background = (resources.getDrawable(R.drawable.profile_gradient))

            pastDur.setTextSize(14f)
            pastDur.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}
