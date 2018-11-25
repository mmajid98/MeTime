package com.metime

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.metime.ListChallenges.SetChallengeActivity
import com.metime.ProfilePage.ProfileActivity
import com.metime.ProfilePage.UsageContract
import com.metime.setChallenge.ChallengeActivity
import com.socialtime.UsagePresenter
import com.socialtime.UsageStatsWrapper


class MainActivity : AppCompatActivity() , UsageContract.View{
    override fun onUsageStatsRetrieved(list: MutableList<UsageStatsWrapper>) {
    }

    override fun onUserHasNoPermission() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var presenter: UsageContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        val user = fireAuth.currentUser
        presenter = UsagePresenter(this, this, 1)
        presenter.retrieveUsageStats(-1)
        if (user != null) {
            Toast.makeText(this, "Logging Current User", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SetChallengeActivity::class.java))
            finish()
        }
        else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
