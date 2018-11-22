package com.metime

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.metime.ProfilePage.ProfileActivity


class MainActivity : AppCompatActivity() {
    private lateinit var fireAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        val user = fireAuth.currentUser
        if (user != null) {
            Toast.makeText(this, "Logging Current User", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
