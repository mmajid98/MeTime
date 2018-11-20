package com.metime.LoginRegisterReset

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.metime.LoginActivity
import com.metime.R

class LoginFragment : android.support.v4.app.Fragment() {

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var mview : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        fireAuth = FirebaseAuth.getInstance()
        val user = fireAuth.currentUser
        if (user != null) {
            Toast.makeText(this.activity, "Logging Current User", Toast.LENGTH_LONG).show()
            //startActivity(Intent(this@LoginActivity, NavigationActivity::class.java))
        }
        mview =  inflater.inflate(R.layout.login_fragment, container, false)

        val email_but = mview.findViewById<Button>(R.id.login_button)
        val reg_but = mview.findViewById<TextView>(R.id.login_register)
        val fb_but = mview.findViewById<ImageView>(R.id.login_facebook)
        val google_but = mview.findViewById<ImageView>(R.id.login_google)
        val forgot_but = mview.findViewById<TextView>(R.id.forgot_password)

        email_but.setOnClickListener(clickListener)
        reg_but.setOnClickListener(clickListener)
        fb_but.setOnClickListener(clickListener)
        google_but.setOnClickListener(clickListener)
        forgot_but.setOnClickListener(clickListener)
        return mview
    }

    private val clickListener = View.OnClickListener { view ->
        when(view.id) {
            R.id.login_button -> {
                val email = mview.findViewById<EditText>(R.id.login_email).text.toString()
                val password = mview.findViewById<EditText>(R.id.login_password).text.toString()

                fireAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this.activity, "Successful Logged In.", Toast.LENGTH_LONG).show()
                    }
                    else {
                        mview.findViewById<EditText>(R.id.login_password).setText("")
                        Toast.makeText(this.activity, "Unsuccessful Login. <" + task.exception!!.message.toString() + " >", Toast.LENGTH_LONG).show()
                    }
                }

            }
            R.id.login_register -> {
                //startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            R.id.login_facebook -> {
                Toast.makeText(this.activity, "fb pressed.", Toast.LENGTH_LONG).show()
            }

            R.id.login_google -> {
                Toast.makeText(this.activity, "google pressed.", Toast.LENGTH_LONG).show()
            }

            R.id.forgot_password -> {
                Toast.makeText(this.activity, "forgot pressed.", Toast.LENGTH_LONG).show()
                setFragment(ResetFragment())
            }
        }
    }

    private fun setFragment(fragment: Fragment): Boolean {
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
        return true
    }

}