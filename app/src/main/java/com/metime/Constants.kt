package com.metime

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.metime.LoginRegisterReset.MeProfile
import com.socialtime.UsageStatsWrapper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.doAsync

public class Constants {
    companion object {
        var customer : Boolean = false
        var pieData = mutableListOf<PieEntry>()
        var profile : MeProfile = MeProfile()
        var image : String? = null
        var dataList = listOf<UsageStatsWrapper>()

        fun setupPic() {
            val fireAuth = FirebaseAuth.getInstance()
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val databaseReference = firebaseDatabase.getReference("Users").child(fireAuth.uid!!)
            val eventListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.i("Constants", "Could not get photo")
                }
                @SuppressLint("SetTextI18n")
                override fun onDataChange(p0: DataSnapshot) {
                    profile = p0.getValue(MeProfile::class.java) as MeProfile
                    if (profile.image != "") {
                        image = profile.image
                    }
                    else {
                        FirebaseStorage.getInstance().getReference()
                                .child("Profiles").child(fireAuth.uid!!).child("Profile Pic")
                                .downloadUrl.addOnSuccessListener {
                            image = it.toString()
                            doAsync { databaseReference.child("image").setValue(image) }
                        }
                    }
                }
            }
            databaseReference.addValueEventListener(eventListener)
        }
    }
}