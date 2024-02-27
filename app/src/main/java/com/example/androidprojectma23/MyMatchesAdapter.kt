package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyMatchesAdapter(private var users: List<User>) : RecyclerView.Adapter<MyMatchesAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val displayNameTextView: TextView = view.findViewById(R.id.myMatches_userDisplayName)
        private val userProfileImage: ImageView = view.findViewById(R.id.myMatches_userProfileImage)
        private val iconImages = listOf<ImageView>(
            view.findViewById(R.id.iconImage1),
            view.findViewById(R.id.iconImage2),
            view.findViewById(R.id.iconImage3),
            view.findViewById(R.id.iconImage4),
            view.findViewById(R.id.iconImage5)
        )
        private val iconTexts = listOf<TextView>(
            view.findViewById(R.id.icon1text),
            view.findViewById(R.id.icon2text),
            view.findViewById(R.id.icon3text),
            view.findViewById(R.id.icon4text),
            view.findViewById(R.id.icon5text)
        )

        fun bind(user: User) {
            displayNameTextView.text = user.displayName

            Glide.with(itemView.context)
                .load(user.profileImage)
                .placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder)
                .into(userProfileImage)

            // Clear previous icons and texts
            iconImages.forEach { it.setImageDrawable(null) }
            iconTexts.forEach { it.text = "" }


            user.interests.take(iconImages.size).forEachIndexed { index, interestDocId ->
                IconMapping.docIdToIconResMap[interestDocId]?.let { iconResId ->
                    Glide.with(itemView.context)
                        .load(iconResId)
                        .into(iconImages[index])
                }

                IconMapping.docIdToInterestNameMap[interestDocId]?.let { interestNameResId ->
                    iconTexts[index].text = itemView.context.getString(interestNameResId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_matches_item, parent, false)
        return UserViewHolder(view)
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
