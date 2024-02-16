package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val chatMessages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatCardViewHolder>() {

    class ChatCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val messageName: TextView = view.findViewById(R.id.text_message_name)
        val messageBody: TextView = view.findViewById(R.id.text_message_body)
        val messageTime: TextView = view.findViewById(R.id.text_message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_card_layout, parent, false)
        return ChatCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatCardViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.messageName.text = chatMessage.userName
        holder.messageBody.text = chatMessage.messageBody
        holder.messageTime.text = chatMessage.messageTime

    }

    override fun getItemCount() = chatMessages.size
}
