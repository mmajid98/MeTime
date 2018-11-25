package com.metime.setChallenge

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.widget.NumberPicker
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.metime.Constants
import com.metime.ListChallenges.SetChallengeActivity
import com.metime.LoginRegisterReset.MeProfile
import com.metime.ProfilePage.ProfileActivity
import com.metime.R
import com.squareup.picasso.Picasso
import com.super_rabbit.wheel_picker.OnValueChangeListener
import com.super_rabbit.wheel_picker.WheelPicker
import kotlinx.android.synthetic.main.activity_challenge.*
import kotlinx.android.synthetic.main.register_fragment.view.*
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class ChallengeActivity : AppCompatActivity() {
    companion object {
        val URL = "https://api.globalgiving.org/api/public/projectservice/featured/projects/summary?api_key=0a84b8f6-3d65-444d-af4d-884f6a068b10"
    }
    private lateinit var firebaseDatabase : FirebaseDatabase
    var money = 2
    var timeS = 2
    val retList = mutableListOf<SelectCharityModel>()
    var adapterC = SelectCharityAdapter(retList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)
        firebaseDatabase = FirebaseDatabase.getInstance()
        if (Constants.image != null) {
            Picasso.get().load(Constants.image).fit().centerCrop(Gravity.CENTER).into(challenge_photo)
        }
        if (Constants.profile != null) challenge_name.text = Constants.profile?.name
        else doAsync { Constants.setupPic(challenge_photo) }
        loadCharityData()
        challenge_money.scrollTo(2)
        challenge_times.scrollTo(2)
        val adapter = SelectAppsAdapter(getList())
        select_app_recycler.setHasFixedSize(true)
        select_app_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        select_app_recycler.adapter = adapter
        back_but.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        challenge_money.setOnValueChangedListener(object : OnValueChangeListener {
            override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                money = newVal.toInt()
                challenge_tm.text = "$" + newVal
            }
        })
        challenge_times.setOnValueChangedListener(object : OnValueChangeListener {
            override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                timeS = newVal.toInt()
            }
        })

        floatingActionButton.setOnClickListener {

            if (adapter.selected > 0 && adapterC.selPos != -1 && !challenge_message.text.toString().contentEquals("")) {
                sendNewsFeed(money, challenge_message.text.toString())
                val myRef = firebaseDatabase.getReference("Challenges").child(FirebaseAuth.getInstance().currentUser!!.uid).child("started")
                val time = Calendar.getInstance().timeInMillis
                val endTime = time + timeS*60*60*1000L
                val k = myRef.push()
                val myFeed = Challenge(k.key.toString(), money, time, endTime, adapter.returnSelected(), adapterC.getSelectedChar())
                k.setValue(myFeed)
                startActivity(Intent(this, SetChallengeActivity::class.java))
            }
        }
    }

    private fun sendNewsFeed(money: Int, message: String) {
        val myRef = firebaseDatabase.getReference("NewsFeed")
        val time = Calendar.getInstance().timeInMillis
        val myFeed = NewsFeed(money, time, Constants.profile!!.name, Constants.image!!.toString(), message)
        val k = myRef.push()
        k.setValue(myFeed)
    }


    private fun loadCharityData() {
        val stringRequest  = object : StringRequest(
                Request.Method.GET,
                URL,
                Response.Listener<String> {
                    //parseXML(it)
                    val jObject = JSONObject(it)
                    val array = (jObject).getJSONObject("projects").getJSONArray("project")

                    for (i in 1..array.length()) {
                        val o = array.getJSONObject(i-1)
                        val project = SelectCharityModel(o.getString("title"), o.getString("country"), o.getString("themeName"))
                        retList.add(project)
                    }

                    adapterC = SelectCharityAdapter(retList)
                    challenge_charity.setHasFixedSize(true)
                    challenge_charity.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    challenge_charity.adapter = adapterC
                },
                Response.ErrorListener {

                }
        )
        {
            override fun getHeaders() : Map<String, String> {
                val headers = HashMap<String, String>()
                //params.put("Content-Type", "application/json")
                headers["Accept"] = "application/json"
                return headers
            }
        }
        val requestQ = Volley.newRequestQueue(this)
        requestQ.add(stringRequest)
    }

    private fun parseXML(it: String) {
        val parserFactory = XmlPullParserFactory.newInstance()
        val parser = parserFactory.newPullParser()
        val isk = ByteArrayInputStream(it.toByteArray())
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(isk, null)
        processParsing(parser)
    }

    private fun processParsing(parser: XmlPullParser) {
        var eventType = parser.eventType
        var project : SelectCharityModel? = null
        while (eventType != XmlPullParser.END_DOCUMENT) {
            var eltName : String? = null

            when (eventType){
                XmlPullParser.START_TAG -> {
                    eltName = parser.name

                    if ("project" == eltName) {
                        project = SelectCharityModel()
                    }
                    else if(project != null) {
                        if (eltName == "title") {
                            project.fund = parser.nextText()
                            retList.add(project)
                        }
                        else if (eltName == "country") {
                            project.country = parser.nextText()
                        }
                        else if (eltName == "themeName") {
                            project.Ftype = parser.nextText()
                        }
                    }
                }
            }
            eventType = parser.next()
        }
    }

    private fun getList() : List<SelectAppsModel> {
        val temp = mutableListOf<SelectAppsModel>()
        for ( i in Constants.dataList) {
            temp.add(SelectAppsModel(i.appIcon, i.appName, i.packageName))
        }
        return temp
    }
}
