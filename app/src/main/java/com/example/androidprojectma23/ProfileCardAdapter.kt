package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojectma23.IconMapping.docIdToIconResMap
import com.example.androidprojectma23.IconMapping.imageViewIdProfileCard
import java.util.Collections

class ProfileCardAdapter (private var user: MutableList<User>) : RecyclerView.Adapter<ProfileCardAdapter.ProfileViewHolder>(),
    ItemMoveCallback.ItemTouchHelperAdapter {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileCardAdapter.ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_card_layout, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileCardAdapter.ProfileViewHolder, position: Int) {
        val user = this.user[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return this.user.size
    }

    inner class ProfileViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(user: User) {
            val imageToLoad = if (user.profileImage.isBlank()) {
                R.drawable.profile_image_placeholder
            } else {
                user.profileImage
            }

            Glide.with(view.context)
                .load(imageToLoad)
                .placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder)
                .into(view.findViewById(R.id.profileImageView))

            view.findViewById<TextView>(R.id.displayNameTextView).text = user.displayName

            user.interests?.let { interests ->
                // Create and sort a list of interests based on their alpha value
                val sortedInterests = interests.map { interest ->
                    val alpha = if (user.commonInterests.contains(interest)) 1.0f else 0.5f
                    interest to alpha
                }.sortedByDescending { it.second }

                for (i in 0 until minOf(sortedInterests.size, imageViewIdProfileCard.size)) {
                    val (interest, alpha) = sortedInterests[i]
                    val imageView = view.findViewById<ImageView>(imageViewIdProfileCard[i])
                    val iconString = docIdToIconResMap[interest]
                    val imageResourceId = iconString?.let {
                        view.context.resources.getIdentifier(it.toString(), "drawable", view.context.packageName)
                    } ?: R.drawable.icon_empty

                    imageView.setImageResource(imageResourceId)
                    imageView.alpha = alpha
                }

                // Hide redundant ImageView-elements
                for (i in sortedInterests.size until imageViewIdProfileCard.size) {
                    val imageView = view.findViewById<ImageView>(imageViewIdProfileCard[i])
                    imageView.visibility = View.GONE
                }
            }
        }

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(this.user, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        this.user.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateData(newUsers: List<User>) {
        user.clear()
        user.addAll(newUsers)
        notifyDataSetChanged()
    }

}

