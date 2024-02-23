package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojectma23.IconMapping.docIdToIconResMap
import com.example.androidprojectma23.IconMapping.imageViewIdProfileCard
import com.example.androidprojectma23.IconMapping.imageViewIdProfileCardBack
import java.util.Collections

class ProfileCardAdapter (private var user: MutableList<User>) : RecyclerView.Adapter<ProfileCardAdapter.ProfileViewHolder>(),
    ItemMoveCallback.ItemTouchHelperAdapter {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileCardAdapter.ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_card_viewflipper, parent, false)
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
            val viewFlipper = itemView.findViewById<ViewFlipper>(R.id.profileCardBack)

            Glide.with(view.context)
                .load(user.profileImage)
                .placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder) //
                .into(view.findViewById(R.id.profileImageView))

            Glide.with(view.context)
                .load(user.profileImage)
                .placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder) //
                .into(view.findViewById(R.id.profileImageViewBack))

            view.findViewById<TextView>(R.id.displayNameTextView).text = user.displayName
            view.findViewById<TextView>(R.id.displayNameTextViewBack).text = user.displayName

            fun loadInterests(imageViewIds: List<Int>){
            user.interests?.let { interests ->
                for (i in 0 until minOf(interests.size, imageViewIds.size)) {
                    val imageView = view.findViewById<ImageView>(imageViewIds[i])
                    val interestId = interests[i]
                    val iconString = docIdToIconResMap[interestId]
                    val imageResourceId = iconString?.let {
                        view.context.resources.getIdentifier(it.toString(), "drawable", view.context.packageName)
                    } ?: R.drawable.icon_empty

                    imageView.setImageResource(imageResourceId)

                    imageView.alpha = if (user.commonInterests.contains(interestId)) {
                        1.0f
                    } else {
                        0.5f
                    }
                }

                for (i in interests.size until imageViewIds.size) {
                    val imageView = view.findViewById<ImageView>(imageViewIds[i])
                    imageView.visibility = View.GONE
                }
            }
            }

            loadInterests(imageViewIdProfileCard)
            loadInterests(imageViewIdProfileCardBack)

            itemView.setOnClickListener {
                viewFlipper.showNext()
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

