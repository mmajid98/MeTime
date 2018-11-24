package com.metime

import android.net.Uri
import android.view.Gravity
import android.widget.ImageView
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.metime.LoginRegisterReset.MeProfile
import com.socialtime.UsageStatsWrapper
import com.squareup.picasso.Picasso

public class Constants {
    companion object {
        var pieData = mutableListOf<PieEntry>()
        var profile : MeProfile? = null
        var image : Uri? = null
        var dataList = listOf<UsageStatsWrapper>()

        fun setupPic(profilePic : ImageView) {
            val fireAuth = FirebaseAuth.getInstance()
            val firebaseStorage = FirebaseStorage.getInstance()
            val firebaseRef = firebaseStorage.getReference()
            firebaseRef.child("Profiles").child(fireAuth.uid!!).child("Profile Pic").downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).fit().centerCrop(Gravity.CENTER).into(profilePic)
                image = it
            }
        }
    }
}