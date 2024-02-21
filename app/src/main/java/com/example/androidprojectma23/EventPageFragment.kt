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


class EventPageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var events: List<Event>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.eventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = EventAdapter(emptyList())

        recyclerView.adapter = adapter
        firestore = FirebaseFirestore.getInstance()

        fetchEvents()

    }

    private fun fetchEvents() {
        firestore.collection("Events")
            .get()
            .addOnSuccessListener { documents ->
                val eventsList = mutableListOf<Event>()
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val date = document.getString("date") ?: ""
                    val location = document.getString("location") ?: ""
                    val image = document.getString("image") ?: ""
                    eventsList.add(Event(name, description, date, location, image))
                }
                adapter.updateEvents(eventsList)
            }
            .addOnFailureListener { exception ->
                Log.d("!!!", "Error getting documents: ", exception)
            }
    }


}
