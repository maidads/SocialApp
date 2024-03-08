package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChatConversationAdapter(
    private val chatMessages: List<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].isUserMessage) VIEW_TYPE_USER else VIEW_TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_item_user, parent, false)
            UserMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_item_other, parent, false)
            OtherMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        when (holder) {
            is UserMessageViewHolder -> holder.bind(chatMessage)
            is OtherMessageViewHolder -> holder.bind(chatMessage)
        }
    }

    override fun getItemCount() = chatMessages.size

    class UserMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageBody: TextView = view.findViewById(R.id.text_message_body)
        fun bind(chatMessage: ChatMessage) {
            messageBody.text = chatMessage.messageBody
        }
    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageBody: TextView = view.findViewById(R.id.text_message_body)
        private val profileImage: ImageView = view.findViewById(R.id.shapeableImageView)
        fun bind(chatMessage: ChatMessage) {
            messageBody.text = chatMessage.messageBody

            Glide.with(itemView.context)
                .load(chatMessage.profileImageUrl)
                .circleCrop()
                .placeholder(R.drawable.baseline_emoji_emotions_24)
                .error(R.drawable.baseline_emoji_emotions_24)
                .into(profileImage)

        }
    }
}


