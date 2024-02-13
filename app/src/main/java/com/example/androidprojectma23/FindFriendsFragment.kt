package com.example.androidprojectma23

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FindFriendsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val profiles = mutableListOf<Profile>(
            Profile("https://example.com/image1.jpg", "Fotboll, Musik, Matlagning"),
            Profile("https://example.com/image2.jpg", "Resor, Läsning, Träning"),
            Profile("https://example.com/image3.jpg", "Konst, Film, Teknik"),
            Profile("https://example.com/image1.jpg", "Fotboll, Musik, Matlagning"),
            Profile("https://example.com/image2.jpg", "Resor, Läsning, Träning"),
            Profile("https://example.com/image3.jpg", "Konst, Film, Teknik")
        )

        val view = inflater.inflate(R.layout.fragment_find_friends, container, false)

        val adapter = ProfileCardAdapter()
        adapter.setProfiles(profiles)

        val recyclerView = view.findViewById<RecyclerView>(R.id.profilesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter

        val callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)

        return view
    }
}