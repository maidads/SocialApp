package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        val eventId = arguments?.getString("eventId")
        eventId?.let {
            fetchEventDetails(it)
        }
    }

    private fun fetchEventDetails(eventId: String) {
        firestore.collection("Events").document(eventId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val event = document.toObject(Event::class.java)
                    displayEventDetails(event)
                } else {
                    Log.d("EventDetailFragment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("EventDetailFragment", "get failed with ", exception)
            }
    }

    private fun displayEventDetails(event: Event?) {
        event?.let {
            view?.findViewById<TextView>(R.id.detailEventNameTextView)?.text = event.name
            view?.findViewById<TextView>(R.id.detailEventDescriptionTextView)?.text = event.description
            view?.findViewById<TextView>(R.id.detailEventLocationTextView)?.text = event.location
            view?.findViewById<TextView>(R.id.detailEventDateTextView)?.text = event.date

            event.image?.let { imageUrl ->
                view?.findViewById<ImageView>(R.id.detailEventImageView)?.let { imageView ->
                    Glide.with(this).load(imageUrl).into(imageView)
                }
            }
        }
    }
}
