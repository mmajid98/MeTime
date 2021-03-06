package com.metime.Newsfeed

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.metime.Constants
import com.metime.ProfilePage.ProfileActivity
import com.metime.R
import com.metime.setChallenge.ChallengeActivity
import com.metime.setChallenge.NewsFeed
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_newsfeed.*
import kotlinx.android.synthetic.main.newsfeed_item.view.*
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*



class NewsfeedActivity : AppCompatActivity(), SearchFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction() {
        supportFragmentManager.beginTransaction().hide(fragment).commit()
    }

    private var shimmer = false
    private lateinit var firebaseRecAdapter : FirebaseRecyclerAdapter<NewsFeed, FireViewHolder>
    private var timerFrag = false
    private lateinit var fireAuth : FirebaseAuth
    private lateinit var fireDatabase : FirebaseDatabase
    private lateinit var fireRef : DatabaseReference
    private lateinit var fragment : SearchFragment
    private lateinit var t_fragment : TimerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsfeed)
        shimmer = true
        fireAuth = FirebaseAuth.getInstance()
        if (Constants.image != null) doAsync {Constants.setupPic()}
        feedRecyclerView.layoutManager = LinearLayoutManager(this)
        fireDatabase = FirebaseDatabase.getInstance()
        fireRef = fireDatabase.getReference("NewsFeed")
    }

    override fun onStart() {
        super.onStart()
        shimmer_view_container.startShimmerAnimation()
        feed_search.isFocusable = false

        val options = FirebaseRecyclerOptions.Builder<NewsFeed>()
                .setQuery(fireRef.orderByChild("sort").limitToLast(20), NewsFeed::class.java)
                .setLifecycleOwner(this)
                .build()
        firebaseRecAdapter = object : FirebaseRecyclerAdapter<NewsFeed, FireViewHolder>(options) {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FireViewHolder {
                return FireViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.newsfeed_item, p0, false))
            }

            override fun onBindViewHolder(holder: FireViewHolder, position: Int, model: NewsFeed) {
                holder.itemView.feed_name.text = model.name
                holder.itemView.feed_message.text = model.message
                holder.itemView.feed_money.text = "$" + model.money
                Picasso.get().load(model.image).into(holder.itemView.feed_photo)
                holder.itemView.feed_time.text = setDateFormat(model.time)
                if (model.likes > 1) holder.itemView.feed_likes.text = model.likes.toString() + " Likes"
                else if (model.likes == 1) holder.itemView.feed_likes.text = model.likes.toString() + " Like"
                else holder.itemView.feed_likes.text = ""
                if (fireAuth.uid.toString() in model.likesList.keys) {
                    holder.itemView.feed_button.text = "Liked"
                    holder.itemView.feed_button.setTextColor(holder.itemView.resources.getColor(R.color.colorPrimaryDark))
                }
                else {
                    holder.itemView.feed_button.text = "Like"
                    holder.itemView.feed_button.setTextColor(holder.itemView.resources.getColor(R.color.colorAccent))
                }
                holder.itemView.feed_button.setOnClickListener {
                    if (holder.itemView.feed_button.text == "Like") {
                        holder.itemView.feed_button.text = "Liked"
                        holder.itemView.feed_button.setTextColor(holder.itemView.resources.getColor(R.color.colorPrimaryDark))
                        model.likes += 1
                        model.likesList[fireAuth.uid.toString()] = Constants.profile!!.name
                    } else {
                        holder.itemView.feed_button.text = "Like"
                        holder.itemView.feed_button.setTextColor(holder.itemView.resources.getColor(R.color.colorAccent))
                        model.likes -= 1
                        model.likesList.remove(fireAuth.uid.toString())
                    }
                    fireRef.child(model.key).setValue(model)
                }
            }
        }
        feedRecyclerView.adapter = firebaseRecAdapter
        newsToPro.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right)
        }
        newsToSet.setOnClickListener{
            startActivity(Intent(this, ChallengeActivity::class.java))
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left)
        }
        feed_search.setOnQueryTextFocusChangeListener { v, hasFocus ->
            setFragment()
        }
        newsToSet2.setOnClickListener {
            if (!timerFrag) setTimerFragment()
            else {
                supportFragmentManager.beginTransaction().hide(t_fragment).commit()
                timerFrag = false
                (it as FloatingActionButton).setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp))
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (shimmer) {
            shimmer = false
            Handler().postDelayed({
                shimmer_view_container.stopShimmerAnimation()
                shimmer_view_container.visibility = View.GONE
            }, 5000)
        }
    }
    override fun onPause() {
        shimmer_view_container.stopShimmerAnimation()
        super.onPause()
    }

    inner class FireViewHolder(itemView : View) : ViewHolder(itemView){ }

    companion object {
        fun setDateFormat(time : Long) : String {
            val x = (System.currentTimeMillis() - time)
            if (x > 604800000) return SimpleDateFormat("dd MMM, hh:mm a").format(Date(time))
            else if (x > 86400000) return SimpleDateFormat("EEE, hh:mm a").format(Date(time))
            else if (x > 3600000) return String.format("%d hours ago", x/3600000)
            else if (x > 60000) return String.format("%d minutes ago", x/60000)
            else return "Now"
        }
    }


    fun setFragment() {
        fragment = SearchFragment.newInstance()
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.setCustomAnimations(R.anim.anim_up, R.anim.anim_down, R.anim.anim_down, R.anim.anim_up)
        //fragmentTrans.addToBackStack(null)
        fragmentTrans.replace(R.id.search_frame, fragment).commit()
    }

    fun setTimerFragment() {
        t_fragment = TimerFragment()
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.setCustomAnimations(R.anim.anim_down, R.anim.anim_up, R.anim.anim_up, R.anim.anim_down)
        fragmentTrans.replace(R.id.search_timer, t_fragment).commit()
        timerFrag = true
        newsToSet2.setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp))
    }

}




