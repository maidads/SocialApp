package com.example.androidprojectma23

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatConversationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private var chatMessages: MutableList<ChatMessage> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_conversation, container, false)

        recyclerView = view.findViewById(R.id.chat_conversation_recyclerview)
        messageInput = view.findViewById(R.id.chat_message_input)
        sendButton = view.findViewById(R.id.chat_send_button)

        val userName = arguments?.getString("conversationUserName") // Fånga upp användarnamnet som skickats som argument

        // Filtrera eller ladda meddelanden baserat på userName här
        // För nu simulerar vi att alla meddelanden tillhör denna konversation
        simulateReceivingMessages(userName)

        // Initial setup for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ChatConversationAdapter(chatMessages)
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                // Antag att alla meddelanden skickade via denna vy är från användaren
                val newMessage = ChatMessage(userName ?: "Unknown", messageText, "Now", true)
                chatMessages.add(newMessage)
                adapter.notifyItemInserted(chatMessages.size - 1)
                messageInput.text.clear()
                recyclerView.scrollToPosition(chatMessages.size - 1)
            }
        }

        return view
    }

    private fun simulateReceivingMessages(userName: String?) {
        // Simulera att lägga till tidigare meddelanden för denna konversation
        chatMessages.add(ChatMessage(userName ?: "Unknown", "Hello, how are you?", "Yesterday", false))
        chatMessages.add(ChatMessage("You", "I'm good, thanks for asking!", "Yesterday", true))
    }
}

