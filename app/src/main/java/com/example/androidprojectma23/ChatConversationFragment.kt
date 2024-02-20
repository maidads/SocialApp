package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatConversationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var userName: String
    private var isUserMessage = true
    private lateinit var profileImageUrl: String
    private var chatMessages: MutableList<ChatMessage> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_conversation, container, false)

        recyclerView = view.findViewById(R.id.chat_conversation_recyclerview)
        messageInput = view.findViewById(R.id.chat_message_input)
        sendButton = view.findViewById(R.id.chat_send_button)

        // Fetch argument sent from ChatFragment
        val conversationId = arguments?.getString("conversationId").toString()
        val conversationProfileImageUrl = arguments?.getString("conversationProfileImageUrl").toString()
        val conversationUserName = arguments?.getString("conversationUserName").toString()

        // Initial setup for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ChatConversationAdapter(chatMessages)
        recyclerView.adapter = adapter

        getConversation(conversationId, conversationProfileImageUrl, conversationUserName)

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                // Antag att alla meddelanden skickade via denna vy är från användaren
                //val newMessage = ChatMessage(userName ?: "Unknown", messageText, "Now", true)
                //chatMessages.add(newMessage)
                adapter.notifyItemInserted(chatMessages.size - 1)
                messageInput.text.clear()
                recyclerView.scrollToPosition(chatMessages.size - 1)
            }
        }
        return view
    }

    private fun getConversation(conversationId: String, conversationProfileImageUrl: String, conversationUserName: String){
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid.toString()

        db.collection("conversations").document(conversationId).collection("messages")
            .get()
            .addOnSuccessListener {result ->
                val messages = mutableListOf<ChatMessage>()

                for (document in result) {
                    val senderUser = document.getString("userName")

                    if (senderUser == currentUser) {
                        db.collection("users").document(currentUser)
                            .get()
                            .addOnSuccessListener {
                                userName = document.getString("displayName")!!
                                profileImageUrl = document.getString("profileImageUrl")!!
                            }

                    } else {
                        userName = conversationUserName
                        profileImageUrl = conversationProfileImageUrl
                        isUserMessage = false
                    }

                    val messageText = document.getString("messageBody")
                    val messageTime = document.getTimestamp("messageTime")
                    if (messageText != null && messageTime != null) {
                        val chatMessage = ChatMessage(
                            userName,
                            messageText,
                            messageTime,
                            conversationProfileImageUrl,
                            isUserMessage
                        )
                        messages.add(chatMessage)
                    }
                }
                updateRecyclerView(messages)
            }.addOnFailureListener { exception ->
                Log.e("!!!", "Error getting documents: ", exception)
            }

    }

    private fun updateRecyclerView(messages: MutableList<ChatMessage>) {
        chatMessages.clear()
        chatMessages.addAll(messages)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}

