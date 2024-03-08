package com.example.androidprojectma23

import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        val event = Event(
            name = arguments?.getString("name", "") ?: "",
            longDescription = arguments?.getString("longDescription", "") ?: "",
            date = arguments?.getString("date", "") ?: "",
            location = arguments?.getString("location", "") ?: "",
            image = arguments?.getString("image", "") ?: ""
        )

        setTopBarTitle(event.name)

        displayEventDetails(event)
    }

    private fun setTopBarTitle(eventName: String) {
        val topBarActivity = (activity as LandingPageActivity)
        topBarActivity.setTitle(eventName)
    }

    private fun displayEventDetails(event: Event) {
        view?.findViewById<TextView>(R.id.detailEventNameTextView)?.text = event.name
        view?.findViewById<TextView>(R.id.detailEventLongDescriptionTextView)?.text =
            event.longDescription
        view?.findViewById<TextView>(R.id.detailEventDateTextView)?.text = event.date
        view?.findViewById<TextView>(R.id.detailEventLocationTextView)?.text = event.location

        event.image?.let { imageUrl ->
            view?.findViewById<ImageView>(R.id.detailEventImageView)?.let { imageView ->
                Glide.with(this).load(imageUrl).into(imageView)
            }
        }
    }
}
