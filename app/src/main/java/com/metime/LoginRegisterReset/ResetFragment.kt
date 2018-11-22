package com.metime.LoginRegisterReset

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.metime.R
import kotlinx.android.synthetic.main.reset_fragment.view.*

class ResetFragment : android.support.v4.app.Fragment() {

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var mview : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fireAuth = FirebaseAuth.getInstance()
        mview =  inflater.inflate(R.layout.reset_fragment, container, false)

        mview.reset_button.setOnClickListener(clickListener)
        mview.reset_register.setOnClickListener(clickListener)
        mview.reset_facebook.setOnClickListener(clickListener)
        mview.reset_google.setOnClickListener(clickListener)
        mview.remember_password.setOnClickListener(clickListener)
        return mview
    }

    private val clickListener = View.OnClickListener { view ->
        when(view.id) {
            R.id.reset_button -> {

                fireAuth.sendPasswordResetEmail(mview.forgot_email.text.toString()) .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this.activity, "Password reset Email sent.", Toast.LENGTH_LONG).show()
                        setFragment(LoginFragment())
                    }
                    else {
                        Toast.makeText(this.activity, "Unsuccessful. <" + task.exception!!.message.toString() + " >", Toast.LENGTH_LONG).show()
                    }
                }

            }
            R.id.reset_register -> {
                setFragment(RegisterFragment())
            }

            R.id.reset_facebook -> {
                Toast.makeText(this.activity, "fb pressed.", Toast.LENGTH_LONG).show()
            }

            R.id.reset_google -> {
                Toast.makeText(this.activity, "google pressed.", Toast.LENGTH_LONG).show()
            }

            R.id.remember_password -> {
                setFragment(LoginFragment())
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