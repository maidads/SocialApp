package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojectma23.IconMapping.docIdToIconIdMap
import com.example.androidprojectma23.IconMapping.getIconId
import com.example.androidprojectma23.IconMapping.iconIdToDocIdMap
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
            Glide.with(view.context)
                .load(user.profileImage)
                .into(view.findViewById(R.id.profileImageView))

            view.findViewById<TextView>(R.id.displayNameTextView).text = user.displayName
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
        user = newUsers.toMutableList()
        notifyDataSetChanged()
    }

}

