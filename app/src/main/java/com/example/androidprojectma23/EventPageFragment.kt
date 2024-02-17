package com.example.androidprojectma23

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class EventPageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var events: List<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_page, container, false)
    }

}
