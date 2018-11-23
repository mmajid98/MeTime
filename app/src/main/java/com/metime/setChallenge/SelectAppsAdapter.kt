package com.metime.setChallenge

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.metime.R
import kotlinx.android.synthetic.main.select_apps_item.view.*

class SelectAppsAdapter(val mModelList: List<SelectAppsModel>) : RecyclerView.Adapter<SelectAppsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_apps_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = mModelList[position]
        holder.itemView.challenge_icon.setImageDrawable(model.icon)
        holder.itemView.challenge_app_name.text = model.text
        holder.itemView.card_view.setCardBackgroundColor(if (model.isSelected) Color.LTGRAY else Color.WHITE)
        holder.itemView.setOnClickListener {
                model.isSelected = !model.isSelected
                holder.itemView.card_view.setCardBackgroundColor(if (model.isSelected) Color.LTGRAY else Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return mModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}