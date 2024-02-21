package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class FilterMenuAdapter(
    private val interests: List<Int>,
    private val onSelectionChange: (Int) -> Unit
) : RecyclerView.Adapter<FilterMenuAdapter.ViewHolder>() {

    private var selectedCount = 1

    init {
        onSelectionChange(selectedCount)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.iconView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.interest_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = interests[position]
        holder.iconView.setImageResource(item)


        if (position == 0) {
            holder.iconView.alpha = 1f
        } else {
            holder.iconView.alpha = 0.5f
        }

        holder.iconView.setOnClickListener {

            if (holder.iconView.alpha < 1f) {
                holder.iconView.alpha = 1f
                selectedCount++
            } else {
                holder.iconView.alpha = 0.5f
                selectedCount--
            }

            onSelectionChange(selectedCount)
        }
    }

    override fun getItemCount(): Int = interests.size
}


