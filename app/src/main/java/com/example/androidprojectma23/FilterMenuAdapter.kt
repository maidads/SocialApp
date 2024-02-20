package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class FilterMenuAdapter(private val interests: List<Int>) :
    RecyclerView.Adapter<FilterMenuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.iconView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.interest_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.iconView.context
        val iconResId = interests[position]

        holder.iconView.setImageResource(iconResId)

        // Set alpha value based on index position
        if (position == 0) {
            holder.iconView.alpha = 1.0f
        } else {
            holder.iconView.alpha = 0.5f
        }

        holder.iconView.setOnClickListener {
            // Invert the alpha-value when an icon is pressed
            val currentAlpha = holder.iconView.alpha
            val newAlpha = if (currentAlpha == 1.0f) 0.5f else 1.0f
            holder.iconView.alpha = newAlpha
        }


    }

    override fun getItemCount() = interests.size
}
