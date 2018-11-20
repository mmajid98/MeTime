package com.metime

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var fireAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        fireAuth = FirebaseAuth.getInstance()
        val user = fireAuth.currentUser
        if (user != null) {
            Toast.makeText(this, "Logging Current User", Toast.LENGTH_LONG).show()
            finish()
            //startActivity(Intent(this@LoginActivity, NavigationActivity::class.java))
        }
        setContentView(R.layout.activity_login)
        val email_but = findViewById<Button>(R.id.login_button)
        val reg_but = findViewById<TextView>(R.id.login_register)
        val fb_but = findViewById<ImageView>(R.id.login_facebook)
        val google_but = findViewById<ImageView>(R.id.login_google)
        val forgot_but = findViewById<TextView>(R.id.forgot_password)

        email_but.setOnClickListener(clickListener)
        reg_but.setOnClickListener(clickListener)
        fb_but.setOnClickListener(clickListener)
        google_but.setOnClickListener(clickListener)
        forgot_but.setOnClickListener(clickListener)

    }

    private val clickListener = View.OnClickListener { view ->
        when(view.id) {
            R.id.login_button -> {
                val email = findViewById<EditText>(R.id.login_email).text.toString()
                val password = findViewById<EditText>(R.id.login_password).text.toString()

                fireAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successful Logged In.", Toast.LENGTH_LONG).show()
                    }
                    else {
                        findViewById<EditText>(R.id.login_password).setText("")
                        Toast.makeText(this, "Unsuccessful Login. <" + task.exception!!.message.toString() + " >", Toast.LENGTH_LONG).show()
                    }
                }

            }
            R.id.login_register -> {
                //startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            R.id.login_facebook -> {
                Toast.makeText(this, "fb pressed.", Toast.LENGTH_LONG).show()
            }

            R.id.login_google -> {
                Toast.makeText(this, "google pressed.", Toast.LENGTH_LONG).show()
            }

            R.id.forgot_password -> {
                Toast.makeText(this, "forgot pressed.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
