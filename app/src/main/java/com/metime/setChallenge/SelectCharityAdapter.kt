package com.metime.setChallenge

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import com.metime.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.set_charities_item.view.*

class SelectCharityAdapter(val mModelList: List<SelectCharityModel>) : RecyclerView.Adapter<SelectCharityAdapter.MyViewHolder>() {
    var model = SelectCharityModel()
    var selPos = -1
    var curPos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.set_charities_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        model = mModelList[position]
        curPos = position
        holder.itemView.ExploreCountry.text = model.text
        Picasso.get().load(model.icon).into(holder.itemView.ExploreImage)
        holder.itemView.ExploreAdd.isChecked = selPos == position
    }

    override fun getItemCount(): Int {
        return mModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.ExploreAdd.setOnClickListener{
                selPos = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}