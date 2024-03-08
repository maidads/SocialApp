package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class EventPageFragment : Fragment(), EventAdapter.OnEventClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.eventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = EventAdapter(emptyList(), this)
        recyclerView.adapter = adapter
        firestore = FirebaseFirestore.getInstance()

        fetchEvents()

    }

    private fun fetchEvents() {
        firestore.collection("Events")
            .orderBy("date")
            .get()
            .addOnSuccessListener { documents ->
                val eventsList = mutableListOf<Event>()
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val date = document.getString("date") ?: ""
                    val location = document.getString("location") ?: ""
                    val image = document.getString("image") ?: ""
                    val longDescription = document.getString("longDescription") ?: ""
                    eventsList.add(Event(name, description, date, location, image, longDescription))
                }
                adapter.updateEvents(eventsList)
            }
            .addOnFailureListener {
            }
    }

    override fun onEventClick(event: Event) {
        val eventDetailFragment = EventDetailFragment().apply {
            arguments = Bundle().apply {
                putString("name", event.name)
                putString("longDescription", event.longDescription)
                putString("date", event.date)
                putString("location", event.location)
                putString("image", event.image)
            }
        }

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fragmentHolder, eventDetailFragment)
            addToBackStack(EventDetailFragment::class.java.simpleName)
            commit()
        }
    }

}
