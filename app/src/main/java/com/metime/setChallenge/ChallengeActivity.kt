package com.metime.setChallenge

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.metime.Constants
import com.metime.R
import kotlinx.android.synthetic.main.activity_challenge.*
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader

class ChallengeActivity : AppCompatActivity() {
    companion object {
        val URL = "https://api.globalgiving.org/api/public/projectservice/featured/projects/summary?api_key=0a84b8f6-3d65-444d-af4d-884f6a068b10"
    }
    val retList = mutableListOf<SelectCharityModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)
        loadCharityData()
        val adapter = SelectAppsAdapter(getList())
        select_app_recycler.setHasFixedSize(true)
        select_app_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        select_app_recycler.adapter = adapter
    }

    private fun loadCharityData() {
        val stringRequest  = object : StringRequest(
                Request.Method.GET,
                URL,
                Response.Listener<String> {
                    parseXML(it)
                    val adapterC = SelectCharityAdapter(retList)
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
                            project.text = parser.nextText()
                            retList.add(project)
                        }
                        else if (eltName == "imageLink") {
                            project.icon = parser.nextText()
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
            temp.add(SelectAppsModel(i.appIcon, i.appName))
        }
        return temp
    }
}
