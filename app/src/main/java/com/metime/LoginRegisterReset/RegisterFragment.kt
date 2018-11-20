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


class RegisterFragment : android.support.v4.app.Fragment() {

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var name : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var con_password : EditText
    private lateinit var user_pic : ImageView
    private lateinit var user_button : Button
    private lateinit var firebaseStorage : FirebaseStorage
    private lateinit var storageRef : StorageReference
    private lateinit var imagePath : Uri
    companion object {
        const val PICK_IMAGE = 123
    }
    private lateinit var mview : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fireAuth = FirebaseAuth.getInstance()
        mview =  inflater.inflate(R.layout.register_fragment, container, false)

        name = mview.findViewById(R.id.username)
        email = mview.findViewById(R.id.Email_id)
        password = mview.findViewById(R.id.reg_password)
        con_password = mview.findViewById(R.id.reg_password2)

        firebaseStorage = FirebaseStorage.getInstance()
        user_pic = mview.findViewById(R.id.ProfileImage)
        user_button = mview.findViewById(R.id.image_upload)
        user_button.setOnClickListener{
            val intent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE)
        }


        val reg_button = mview.findViewById<Button>( R.id.Reg_button)
        reg_button.setOnClickListener { view->
            if (allStringsValid()) {
                fireAuth.createUserWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim()).addOnCompleteListener{task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this.activity, "Successful Registration", Toast.LENGTH_LONG).show()
                        sendData()
                        setFragment(LoginFragment())
                    }
                    else Toast.makeText(this.activity, "Unsuccessful Registration. <" + task.exception!!.message.toString() + " >", Toast.LENGTH_LONG).show()
                }
            }
        }

        val log_but = mview.findViewById<TextView>(R.id.register_login)
        log_but.setOnClickListener{
            setFragment(LoginFragment())
        }
        return mview
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imagePath = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imagePath)
            user_pic.setImageBitmap(bitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun allStringsValid() : Boolean{

        if (  name.text.toString().isEmpty() || email.text.toString().isEmpty() ||
                password.text.toString().isEmpty() || con_password.text.toString().isEmpty()) {
            Toast.makeText(this.activity, "All fields need to be filled", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!email.text.toString().contains("@")) {
            Toast.makeText(this.activity, "Invalid Email ID", Toast.LENGTH_SHORT).show()
            password.setText("")
            con_password.setText("")
            return false
        }
        if (password.text.toString() != con_password.text.toString()) {
            Toast.makeText(this.activity, "Passwords do not match", Toast.LENGTH_SHORT).show()
            password.setText("")
            con_password.setText("")
            return false
        }
        return true
    }

    private fun sendData() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val myRef = firebaseDatabase.getReference("Users").child(fireAuth.uid!!)
        val myProfile = MeProfile(name.text.toString(), email.text.toString(), "TBD")
        myRef.setValue(myProfile).addOnSuccessListener {

        }
        Toast.makeText(this.activity, "Successful Profile uploaded", Toast.LENGTH_LONG).show()

        storageRef = firebaseStorage.getReference().child("Profiles").child(fireAuth.uid!!).child("Profile Pic")
        val uploadTask = storageRef.putFile(imagePath)
        uploadTask.addOnCompleteListener{
            Toast.makeText(this.activity, "Image Uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this.activity, "Image upload failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFragment(fragment: Fragment): Boolean {
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
        return true
    }
}