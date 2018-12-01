package com.metime.Newsfeed

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.metime.Constants
import com.metime.LoginRegisterReset.MeProfile

import com.metime.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_newsfeed.*
import kotlinx.android.synthetic.main.activity_newsfeed.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.search_item.view.*



class SearchFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var fireDatabase : FirebaseDatabase
    private lateinit var fireRef : DatabaseReference
    private lateinit var SearchRecyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_search, container, false)
        SearchRecyclerView = v.friendrecycler
        SearchRecyclerView.layoutManager = LinearLayoutManager(this.context)

        fireDatabase = FirebaseDatabase.getInstance()
        fireRef = fireDatabase.getReference("Users")

        v.recycler_search.setIconifiedByDefault(false)
        v.recycler_search.requestFocus()

        val options = FirebaseRecyclerOptions.Builder<MeProfile>()
                .setQuery(fireRef, MeProfile::class.java)
                .setLifecycleOwner(this)
                .build()

        val firebaseRecAdapter = object : FirebaseRecyclerAdapter<MeProfile, SearchViewHolder>(options) {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SearchViewHolder {
                return SearchViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.search_item, p0, false))
            }

            override fun onBindViewHolder(holder: SearchViewHolder, position: Int, model: MeProfile) {
                holder.itemView.search_name.text = model.name
                holder.itemView.search_time.text = model.city + ", " + model.country
                Picasso.get().load(model.image).into(holder.itemView.search_photo)
                holder.itemView.search_follow.text = " FOLLOW + "
                holder.itemView.search_follow.setOnClickListener {
                    if (holder.itemView.search_follow.text == " FOLLOW + ") {
                        holder.itemView.search_follow.text = " FOLLOWING "
                    }
                    else holder.itemView.search_follow.text = " FOLLOW + "
                }
            }
        }
        SearchRecyclerView.adapter = firebaseRecAdapter

        v.recycler_search.setOnQueryTextListener(searchlistener)

        v.search_cancel.setOnClickListener{
            if (listener != null) {
                onBackPressed()
            }
        }

        return v
    }

    fun onBackPressed() {
        listener?.onFragmentInteraction()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction()
    }



    companion object {
        @JvmStatic
        fun newInstance() =
                SearchFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    fun fireSearch(searchText : String?) {

        val fireSearchQuery = fireRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff")
        val options = FirebaseRecyclerOptions.Builder<MeProfile>()
                .setQuery(fireSearchQuery, MeProfile::class.java)
                .setLifecycleOwner(this)
                .build()

        val firebaseRecAdapter = object : FirebaseRecyclerAdapter<MeProfile, SearchFragment.SearchViewHolder>(options) {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SearchFragment.SearchViewHolder {
                return SearchViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.search_item, p0, false))
            }

            override fun onBindViewHolder(holder: SearchFragment.SearchViewHolder, position: Int, model: MeProfile) {
                holder.itemView.search_name.text = model.name
                holder.itemView.search_time.text = model.city + ", " + model.country
                Picasso.get().load(model.image).into(holder.itemView.search_photo)
                holder.itemView.search_follow.text = " + FOLLOW "

                holder.itemView.search_follow.setOnClickListener {
                    if (holder.itemView.search_follow.text == " + FOLLOW ") {
                        holder.itemView.search_follow.text = " FOLLOWING "
                    }
                    else holder.itemView.search_follow.text = " + FOLLOW "
                }
            }
        }
        SearchRecyclerView.adapter = firebaseRecAdapter

    }

    val searchlistener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            fireSearch(p0)
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            fireSearch(p0)
            return false
        }
    }
    inner class SearchViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }
}
