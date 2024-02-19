package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment(), ChatAdapter.ChatCardListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private var chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.chat_recycler_view)

        chatAdapter = ChatAdapter(chatMessages, this)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        getConversations()

        return view
    }

    private fun getConversations() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val currentUserId = currentUser.uid

            db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Get list of conversationIds from current user
                        val conversationIds = document.get("userConversations") as? List<String> ?: listOf()
                        Log.d("MyApp", "Document: $document")
                        // For every conversationId...
                        for (conversationId in conversationIds) {
                            // Get conversation document
                            db.collection("conversations").document(conversationId)
                                .get()
                                .addOnSuccessListener { conversationDocument ->
                                        val lastMessageText = conversationDocument.getString("lastmessageText")
                                        val lastMessageTime = conversationDocument.getString("lastmessageTime")

                                        if (lastMessageTime != null && lastMessageText != null) {

                                            val chatMessage = ChatMessage("Conversation", lastMessageText, lastMessageTime, true)
                                            chatMessages.add(chatMessage)
                                            chatAdapter.notifyDataSetChanged()
                                        }

                                }
                        }
                    }

                }
        } else {
            // hantera situationen när användaren är null
        }



    }


    override fun onChatCardClicked(position: Int) {
        val chatMessage = chatMessages[position]
        openChatConversationFragment(chatMessage)
    }

    private fun openChatConversationFragment(chatMessage: ChatMessage) {
        val chatConversationFragment = ChatConversationFragment().apply {
            arguments = Bundle().apply {
                // Antag att vi skickar användarnamnet som ett sätt att identifiera konversationen
                // I en riktig applikation kan detta vara ett unikt ID för konversationen eller användaren
                putString("conversationUserName", chatMessage.userName)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, chatConversationFragment)
            .addToBackStack(null)  // Lägger till transaktionen till backstack för att möjliggöra navigering tillbaka
            .commit()
    }
}