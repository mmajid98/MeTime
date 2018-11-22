package com.metime.LoginRegisterReset

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.metime.ProfilePage.ProfileActivity
import com.metime.R
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginFragment : android.support.v4.app.Fragment() {

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var mview : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        fireAuth = FirebaseAuth.getInstance()
//        val user = fireAuth.currentUser
//        if (user != null) {
//            Toast.makeText(this.activity, "Logging Current User", Toast.LENGTH_LONG).show()
//            startActivity(Intent(this.activity, ProfileActivity::class.java))
//        }

        mview =  inflater.inflate(R.layout.login_fragment, container, false)
        mview.login_button.setOnClickListener(clickListener)
        mview.login_register.setOnClickListener(clickListener)
        mview.login_facebook.setOnClickListener(clickListener)
        mview.login_google.setOnClickListener(clickListener)
        mview.login_password.setOnClickListener(clickListener)
        return mview
    }

    private val clickListener = View.OnClickListener { view ->
        when(view.id) {
            R.id.login_button -> {
                fireAuth.signInWithEmailAndPassword(
                        mview.login_email.text.toString().trim(),
                        mview.login_password.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this.activity, "Successful Logged In.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this.activity, ProfileActivity::class.java))
                    }
                    else {
                        mview.login_password.setText("")
                        Toast.makeText(this.activity, "Unsuccessful Login. <" + task.exception!!.message.toString() + " >", Toast.LENGTH_LONG).show()
                    }
                }
            }
            R.id.login_register -> {
                setFragment(RegisterFragment())
            }

            R.id.login_facebook -> {
                Toast.makeText(this.activity, "fb pressed.", Toast.LENGTH_LONG).show()
            }

            R.id.login_google -> {
                Toast.makeText(this.activity, "google pressed.", Toast.LENGTH_LONG).show()
            }

            R.id.login_password -> {
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