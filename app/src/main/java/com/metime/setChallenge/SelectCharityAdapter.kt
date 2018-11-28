package com.metime.setChallenge

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        when(position % 2) {
            0 -> holder.itemView.ExploreLayout.background = holder.itemView.context.getDrawable(R.drawable.profile_gradient)
            1 ->holder.itemView.ExploreLayout.background = holder.itemView.context.getDrawable(R.drawable.purple_gradient)
        }

        holder.itemView.ExploreFund.text = model.fund
        holder.itemView.exploreCountryName.text = model.country
        holder.itemView.exploreCountry.text = model.Ftype
        if (selPos == position) {
            holder.itemView.ExploreAdd.isChecked = true
            holder.itemView.ExploreAdd.thumbTintList = ColorStateList.valueOf(holder.itemView.resources.getColor(android.R.color.holo_orange_light))
        }
        else {
            holder.itemView.ExploreAdd.isChecked = false
            holder.itemView.ExploreAdd.thumbTintList = ColorStateList.valueOf(Color.LTGRAY)
        }
    }

    override fun getItemCount(): Int {
        return mModelList.size
    }

    fun getSelectedChar() : String {
        return mModelList[curPos].fund
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