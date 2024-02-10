package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class ProfileCardAdapter : RecyclerView.Adapter<ProfileCardAdapter.ProfileViewHolder>(),
    ItemMoveCallback.ItemTouchHelperAdapter {
    private var profiles: MutableList<Profile> = mutableListOf()

    fun setProfiles(profiles: MutableList<Profile>) {
        this.profiles = profiles
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileCardAdapter.ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_card_layout, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileCardAdapter.ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        holder.bind(profile)
    }

    override fun getItemCount(): Int {
        return profiles.size
    }

    inner class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
        val interestsTextView: TextView = view.findViewById(R.id.interestsTextView)
        val interestsContentTextView: TextView = view.findViewById(R.id.interestsContentTextView)

        fun bind(profile: Profile) {
            // Fill with data from profile to views
            // profileImageView.setImageResource(profile.image)
            //
            interestsContentTextView.text = profile.interests
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(profiles, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        profiles.removeAt(position)
        notifyItemRemoved(position)
    }


}

