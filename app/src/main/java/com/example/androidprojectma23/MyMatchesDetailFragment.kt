package com.example.androidprojectma23

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MyMatchesDetailFragment : Fragment() {
    companion object {
        private const val ARG_USER_ID = "userId"

        fun newInstance(userId: String): MyMatchesDetailFragment {
            val args = Bundle().apply {
                putString(ARG_USER_ID, userId)
            }
            return MyMatchesDetailFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val userId = arguments?.getString(ARG_USER_ID)
        return inflater.inflate(R.layout.fragment_my_matches_details, container, false)
    }
}