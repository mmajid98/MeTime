package com.metime.setChallenge

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.metime.R
import kotlinx.android.synthetic.main.select_apps_item.view.*

class SelectAppsAdapter(val mModelList: List<SelectAppsModel>) : RecyclerView.Adapter<SelectAppsAdapter.MyViewHolder>() {
    var selected = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_apps_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = mModelList[position]
        holder.itemView.challenge_icon.setImageDrawable(model.icon)
        holder.itemView.challenge_app_name.text = model.text
        holder.itemView.card_view.setCardBackgroundColor(holder.itemView.resources.getColor(if (model.isSelected) android.R.color.holo_blue_bright else android.R.color.transparent))
        holder.itemView.setOnClickListener {
                model.isSelected = !model.isSelected
            if (model.isSelected) {
                selected++
                holder.itemView.card_view.setCardBackgroundColor(holder.itemView.resources.getColor(android.R.color.holo_blue_bright))
            }
            else {
                selected--
                holder.itemView.card_view.setCardBackgroundColor(holder.itemView.resources.getColor(android.R.color.transparent))
            }
        }
    }

    override fun getItemCount(): Int {
        return mModelList.size
    }

    fun returnSelected() : List<String> {
        val k = mutableListOf<String>()
        for (i in mModelList) {
            if (i.isSelected) k.add(i.packageName)
        }
        return k
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}