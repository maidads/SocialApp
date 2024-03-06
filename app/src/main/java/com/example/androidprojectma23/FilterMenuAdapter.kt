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
    private var highestSelectedIndex = 0

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
        val context = holder.iconView.context
        val resourceName = "icon_${position + 1}" // Dynamiskt namn baserat på position vid bindning
        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        holder.iconView.setImageResource(resourceId)

        // Initialt sätt opaciteten baserat på det högsta valda indexet
        holder.iconView.alpha = if (position <= highestSelectedIndex) 1f else 0.5f

        holder.iconView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                highestSelectedIndex = currentPosition // Använd den aktuella positionen
                notifyDataSetChanged() // Uppdatera alla ikoner för att reflektera det nya valet
                onSelectionChange(highestSelectedIndex + 1) // Callback för uppdaterad logik
            }
        }
    }

    override fun getItemCount(): Int = interests.size

}


