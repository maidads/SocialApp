package com.example.androidprojectma23

import android.graphics.drawable.Icon
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyMatchesAdapter(private var users: List<User>, private val onUserClick: (String) -> Unit) :
    RecyclerView.Adapter<MyMatchesAdapter.UserViewHolder>() {

    class UserViewHolder(view: View, private val onClick: (String) -> Unit) :
        RecyclerView.ViewHolder(view) {

        private val cardView: CardView = view.findViewById(R.id.myMatchesCard)
        private val displayNameTextView: TextView =
            view.findViewById(R.id.myMatches_userDisplayName)
        private val userProfileImage: ImageView = view.findViewById(R.id.myMatches_userProfileImage)

        fun bind(user: User) {
            displayNameTextView.text = user.displayName

            Glide.with(itemView.context)
                .load(user.profileImage)
                .placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder)
                .into(userProfileImage)

            // Clear previous icons and texts
            IconMapping.fragmentInterestIconImage.forEach { iconId ->
                itemView.findViewById<ImageView>(iconId).setImageDrawable(null)
            }

            IconMapping.fragmentInterestIconText.forEach { textId ->
                itemView.findViewById<TextView>(textId).text = ""
            }


            user.interests.take(IconMapping.fragmentInterestIconImage.size)
                .forEachIndexed { index, interestDocId ->
                    IconMapping.docIdToIconResMap[interestDocId]?.let { iconResId ->
                        Glide.with(itemView.context)
                            .load(iconResId)
                            .into(itemView.findViewById(IconMapping.fragmentInterestIconImage[index]))
                    }

                    IconMapping.docIdToInterestNameMap[interestDocId]?.let { interestNameResId ->
                        itemView.findViewById<TextView>(IconMapping.fragmentInterestIconText[index]).text =
                            itemView.context.getString(interestNameResId)
                    }
                }

            cardView.setOnClickListener {
                onClick(user.userId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.my_matches_item, parent, false)
        return UserViewHolder(view, onUserClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
