package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class InterestIconMenuAdapter(private val interests: List<Int>) : RecyclerView.Adapter<InterestIconMenuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.iconView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.interest_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.iconView.context
        val iconResId = interests[position]
        holder.iconView.setImageResource(iconResId)

    }

    override fun getItemCount() = interests.size
}
