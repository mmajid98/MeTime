package com.socialtime

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.metime.R

class UsageStatsAdapter : RecyclerView.Adapter<UsageStatsVH>() {
    private var list: List<UsageStatsWrapper> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageStatsVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.usage_stat_item, parent, false)
        return UsageStatsVH(view)
    }

    override fun onBindViewHolder(holder: UsageStatsVH, position: Int) {
        holder.bindTo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: List<UsageStatsWrapper>) {
        this.list = list
        notifyDataSetChanged()
    }
}