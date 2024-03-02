package com.example.androidprojectma23

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.health.connect.ReadRecordsRequestUsingIds
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojectma23.IconMapping.docIdToIconResMap
import com.example.androidprojectma23.IconMapping.docIdToInterestNameMap
import com.example.androidprojectma23.IconMapping.imageViewIdProfileCard
import com.example.androidprojectma23.IconMapping.imageViewIdProfileCardBack
import com.example.androidprojectma23.IconMapping.textViewIdProfileCard
import com.example.androidprojectma23.IconMapping.textViewIdProfileCardBack
import java.util.Collections

class ProfileCardAdapter (
    private var user: MutableList<User>,
    private val newMessageButtonClickListener: NewMessageButtonClickListener,
    private val onLeftSwipe: (String) -> Unit)
 : RecyclerView.Adapter<ProfileCardAdapter.ProfileViewHolder>(),

    ItemMoveCallback.ItemTouchHelperAdapter {

    interface NewMessageButtonClickListener {
        fun onNewMessageButtonClicked(otherUser: User)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileCardAdapter.ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_card_viewflipper, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileCardAdapter.ProfileViewHolder, position: Int) {
        val user = this.user[position]
        holder.bind(user, newMessageButtonClickListener)
    }

    override fun getItemCount(): Int {
        return this.user.size
    }

    inner class ProfileViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(user: User, newMessageButtonClickListener: NewMessageButtonClickListener) {

            view.findViewById<Button>(R.id.newMessagebutton).setOnClickListener {
                newMessageButtonClickListener.onNewMessageButtonClicked(user)
            }

            val viewFlipper = itemView.findViewById<ViewFlipper>(R.id.profileCardBack)
            val cardContainer = view.findViewById<View>(R.id.profile_card_viewflipper)
            val frontImage = view.findViewById<ImageView>(R.id.profileImageView)
            val backImage = view.findViewById<ImageView>(R.id.profileImageViewBack)

            fun flipCard(){
                val scale = view.resources.displayMetrics.density
                cardContainer.cameraDistance = 10000 * scale
                val firstHalfFlip = ObjectAnimator.ofFloat(cardContainer, "rotationY", 0f, 100f)
                firstHalfFlip.duration = 250
                firstHalfFlip.interpolator = AccelerateInterpolator()
                firstHalfFlip.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        viewFlipper.showNext()

                        val secondHalfFlip = ObjectAnimator.ofFloat(cardContainer, "rotationY", -100f, 0f)
                        secondHalfFlip.duration = 250
                        secondHalfFlip.interpolator = DecelerateInterpolator()
                        secondHalfFlip.start()
                    }
                })
                firstHalfFlip.start()
            }

            fun loadProfileImage(imageView: ImageView) {
                Glide.with(view.context)
                    .load(user.profileImage)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .error(R.drawable.profile_image_placeholder) //
                    .into(imageView)
            }

            fun loadInterestsText(textViewIds: List<Int>) {
                user.interests.let { interests ->
                    for (i in 0 until minOf(interests.size, textViewIds.size)) {
                        val textView = view.findViewById<TextView>(textViewIds[i])
                        val interestId = interests[i]
                        val textResId = docIdToInterestNameMap[interestId]
                        textView.text = textResId?.let { view.context.getString(it) } ?: view.context.getString(R.string.unknown_interest)


                        textView.alpha = if (user.commonInterests.contains(interestId)) 1.0f else 0.5f
                    }


                    for (i in interests.size until textViewIds.size) {
                        val textView = view.findViewById<TextView>(textViewIds[i])
                        textView.visibility = View.GONE
                    }
                }
            }


            fun loadInterests(imageViewIds: List<Int>){

//                Difference from last code:
//                val sortedInterests = interestsID.map { interest ->
//                    val alpha = if (user.commonInterests.contains(interest)) 1.0f else 0.5f
//                    interest to alpha
//                }.sortedByDescending { it.second }

                user.interests.let { interests ->
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

            itemView.setOnClickListener {
                flipCard()
            }

            //Load and set all information to show on card
            loadInterests(imageViewIdProfileCard)
            loadInterests(imageViewIdProfileCardBack)
            loadInterestsText(textViewIdProfileCard)
            loadInterestsText(textViewIdProfileCardBack)

            loadProfileImage(frontImage)
            loadProfileImage(backImage)

            view.findViewById<TextView>(R.id.displayNameTextView).text = user.displayName
            view.findViewById<TextView>(R.id.displayNameTextViewBack).text = user.displayName

//          Set user age here:
//          view.findViewById<TextView>(R.id.ageTextViewBack).text = view.context.getString(R.string.age_placeholder, age)
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

    override fun onLeftSwipe(position: Int) {
        val userId = user[position].userId
        SwipeDataManager.addUser(userId) // Add user-id in the list
        user.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onRightSwipe(position: Int) {
        this.user.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateData(newUsers: List<User>) {
        user.clear()
        user.addAll(newUsers)
        notifyDataSetChanged()
    }

}

