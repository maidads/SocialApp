package com.example.androidprojectma23

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(private var events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(event: Event) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }
}