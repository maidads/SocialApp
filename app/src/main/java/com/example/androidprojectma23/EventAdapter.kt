package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EventAdapter(private var events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        private val eventImageView: ImageView = itemView.findViewById(R.id.eventImageView)
        private val eventDescriptionTextView: TextView = itemView.findViewById(R.id.eventDescriptionTextView)
        private val eventDateTextView: TextView = itemView.findViewById(R.id.eventDateTextView)
        private val eventLocationTextView: TextView = itemView.findViewById(R.id.eventLocationTextView)

        fun bind(event: Event) {
            eventNameTextView.text = event.name
            eventDescriptionTextView.text = event.description
            eventDateTextView.text = event.date
            eventLocationTextView.text = event.location
            Glide.with(itemView.context).load(event.image).into(eventImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_more_info, parent, false)
        return EventViewHolder(view)
    }

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}