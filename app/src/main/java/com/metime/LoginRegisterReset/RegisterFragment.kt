package com.metime.LoginRegisterReset

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.metime.R
import android.content.Intent
import android.provider.MediaStore
import com.google.firebase.database.FirebaseDatabase
import com.metime.Newsfeed.NewsfeedActivity
import kotlinx.android.synthetic.main.register_fragment.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RegisterFragment : android.support.v4.app.Fragment() {

    private lateinit var fireAuth : FirebaseAuth

    private lateinit var imagePath : Uri
    companion object {
        const val PICK_IMAGE = 123
    }
    private lateinit var mview : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fireAuth = FirebaseAuth.getInstance()
        mview =  inflater.inflate(R.layout.register_fragment, container, false)

        mview.image_upload.setOnClickListener{
            val intent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE)
        }

        mview.Reg_button.setOnClickListener { view->
            if (allStringsValid()) {
                fireAuth.createUserWithEmailAndPassword(mview.Email_id.text.toString().trim(), mview.reg_password.text.toString().trim()).addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this.activity, "Successful Registration", Toast.LENGTH_LONG).show()
                        sendData()
                        startActivity(Intent(this.activity, NewsfeedActivity::class.java))
                    }
                    else Toast.makeText(this.activity, "Unsuccessful Registration. <" + task.exception!!.message.toString() + " >", Toast.LENGTH_LONG).show()
                }
            }
        }
        mview.register_login.setOnClickListener{
            setFragment(LoginFragment())
        }
        return mview
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imagePath = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imagePath)
            mview.ProfileImage.setImageBitmap(bitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun allStringsValid() : Boolean{

        if (mview.username.text.toString().isEmpty() || mview.Email_id.text.toString().isEmpty() ||
                mview.reg_password.text.toString().isEmpty() || mview.reg_password2.text.toString().isEmpty() ||
                mview.usercity.text.toString().isEmpty() || mview.usercountry.text.toString().isEmpty()) {
            Toast.makeText(this.activity, "All fields need to be filled", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!mview.Email_id.text.toString().contains("@")) {
            Toast.makeText(this.activity, "Invalid Email ID", Toast.LENGTH_SHORT).show()
            mview.reg_password.setText("")
            mview.reg_password2.setText("")
            return false
        }
        if (mview.reg_password.text.toString() != mview.reg_password2.text.toString()) {
            Toast.makeText(this.activity, "Passwords do not match", Toast.LENGTH_SHORT).show()
            mview.reg_password.setText("")
            mview.reg_password2.setText("")
            return false
        }
        return true
    }

    private fun sendData() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val myRef = firebaseDatabase.getReference("Users").child(fireAuth.uid!!)
        val myProfile = MeProfile("",
                mview.username.text.toString(),
                mview.usercity.text.toString(),
                mview.usercountry.text.toString()
        )
        myRef.setValue(myProfile)
        val storageRef = FirebaseStorage.getInstance().getReference().child("Profiles").child(fireAuth.uid!!).child("Profile Pic")
        storageRef.putFile(imagePath).addOnSuccessListener {
            Toast.makeText(this.activity, "Successful Profile uploaded", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this.activity, "Image upload failed. Please check your connection or try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFragment(fragment: Fragment): Boolean {
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
        return true
    }
}