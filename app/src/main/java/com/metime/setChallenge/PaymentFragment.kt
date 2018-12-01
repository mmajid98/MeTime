package com.metime.setChallenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.R.attr.button
import android.app.Activity
import android.support.v4.app.Fragment
import com.metime.R
import kotlinx.android.synthetic.main.layout_payment.view.*


class PaymentFragment : Fragment() {

    private lateinit var someEventListener: onSomeEventListener

    internal val LOG_TAG = "myLogs"

    interface onSomeEventListener {
        fun someEvent(s: Int)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            someEventListener = activity as onSomeEventListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement onSomeEventListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.layout_payment, null)

        v.buttonPopup2.setOnClickListener{
            someEventListener.someEvent(1)
        }

        v.buttonPopup.setOnClickListener{
            someEventListener.someEvent(0)
        }

        return v
    }
}