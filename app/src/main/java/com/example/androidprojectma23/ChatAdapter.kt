package com.example.androidprojectma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class ChatAdapter(private val chatMessages: List<ChatMessage>,
                  private val clickListener: ChatCardListener) :
    RecyclerView.Adapter<ChatAdapter.ChatCardViewHolder>() {

    interface ChatCardListener {
        fun onChatCardClicked(position: Int)
    }

    class ChatCardViewHolder(view: View, private val clickListener: ChatCardListener) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                clickListener.onChatCardClicked(adapterPosition)
            }
        }

        val profileImage: ImageView = view.findViewById(R.id.profile_image)
        val messageName: TextView = view.findViewById(R.id.text_message_name)
        val messageBody: TextView = view.findViewById(R.id.text_message_body)
        val messageTime: TextView = view.findViewById(R.id.text_message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_card_layout, parent, false)
        return ChatCardViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ChatCardViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.messageName.text = chatMessage.userName
        holder.messageBody.text = chatMessage.messageBody
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val messageTimeAsString = sdf.format(chatMessage.messageTime.toDate())
        holder.messageTime.text = messageTimeAsString
    }

    override fun getItemCount() = chatMessages.size
}
