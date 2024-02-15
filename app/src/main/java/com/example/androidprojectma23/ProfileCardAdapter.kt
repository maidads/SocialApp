package com.example.androidprojectma23

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class ProfileCardAdapter (private val matchingFriendsList: List<User>) : RecyclerView.Adapter<ProfileCardAdapter.ProfileViewHolder>(),
    ItemMoveCallback.ItemTouchHelperAdapter {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileCardAdapter.ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_card_layout, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileCardAdapter.ProfileViewHolder, position: Int) {
        val user = this.matchingFriendsList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return this.matchingFriendsList.size
    }

    inner class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
        val interestsTextView: TextView = view.findViewById(R.id.interestsTextView)
        val interestsContentTextView: TextView = view.findViewById(R.id.interestsContentTextView)

        fun bind(user: User) {
            // Fill with data from profile to views
            // profileImageView.setImageResource(profile.image)
            //
            interestsContentTextView.text = user.interests ?: ""
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(this.matchingFriendsList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        //this.matchingFriendsList.removeAt(position)
        notifyItemRemoved(position)
    }


}

