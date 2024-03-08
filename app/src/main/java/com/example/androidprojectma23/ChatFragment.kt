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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment(), ChatAdapter.ChatCardListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var latestMessage: DocumentSnapshot
    private lateinit var conversationIds: MutableList<String>
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
        val tempChatMessages = mutableListOf<ChatMessage>()

        if (currentUser != null) {
            val currentUserId = currentUser.uid
            var conversationsLoadedCount = 0

            db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Get list of conversationIds from current user
                        this.conversationIds =
                            document.get("userConversations") as? MutableList<String>
                                ?: mutableListOf()
                        Log.d("MyApp", "Document: $document")
                        // For every conversationId...
                        for (conversationId in conversationIds) {
                            // Get conversation document
                            db.collection("conversations").document(conversationId)
                                .get()
                                .addOnSuccessListener {
                                    val userID1 = it.getString("userID1")
                                    val userID2 = it.getString("userID2")
                                    val otherUserId =
                                        if (userID1 != currentUserId) userID1 else userID2

                                    if (otherUserId != null) {
                                        db.collection("users").document(otherUserId)
                                            .get()
                                            .addOnSuccessListener {
                                                val otherUserName = it.get("displayName").toString()
                                                val profileImageUrl =
                                                    it.get("profileImageUrl").toString()

                                                getLastMessage(conversationId) { latestMessage ->
                                                    var lastMessageText =
                                                        latestMessage.getString("messageBody")
                                                    val lastMessageTime =
                                                        latestMessage.getTimestamp("messageTime")

                                                    if (lastMessageTime != null && lastMessageText != null) {
                                                        val maxLength = 38
                                                        if (lastMessageText.length > maxLength) {
                                                            lastMessageText =
                                                                lastMessageText.substring(
                                                                    0,
                                                                    maxLength
                                                                ) + "..."
                                                        }

                                                        val chatMessage = ChatMessage(
                                                            otherUserId,
                                                            otherUserName,
                                                            lastMessageText,
                                                            lastMessageTime,
                                                            profileImageUrl,
                                                            true
                                                        )
                                                        tempChatMessages.add(chatMessage)
                                                    }
                                                    conversationsLoadedCount++
                                                    if (conversationsLoadedCount == conversationIds.size) {
                                                        val sortedChatMessages =
                                                            tempChatMessages.sortedByDescending { chatMessage -> chatMessage.messageTime }
                                                        chatMessages.clear()
                                                        chatMessages.addAll(sortedChatMessages)
                                                        chatAdapter.notifyDataSetChanged()
                                                    }
                                                }
                                            }
                                    }
                                }
                        }

                    }
                }
        }

    }

    private fun getLastMessage(conversationId: String, callback: (DocumentSnapshot) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("conversations").document(conversationId).collection("messages")
            .orderBy("messageTime", Query.Direction.DESCENDING).limit(1).get()
            .addOnSuccessListener { latestMessageSnapshot ->
                if (!latestMessageSnapshot.isEmpty) {
                    val latestMessage = latestMessageSnapshot.documents[0]
                    callback.invoke(latestMessage)
                }
            }
    }

    override fun onChatCardClicked(position: Int) {
        val chatMessage = chatMessages[position]
        openChatConversationFragment(chatMessage)
    }

    private fun openChatConversationFragment(chatMessage: ChatMessage) {
        val chatConversationFragment = ChatConversationFragment().apply {
            arguments = Bundle().apply {
                putString("conversationUserId", chatMessage.userId)
                putString("conversationProfileImageUrl", chatMessage.profileImageUrl)
                putString("conversationUserName", chatMessage.userName)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, chatConversationFragment)
            .addToBackStack(ChatConversationFragment::class.java.simpleName)  // Add transaction to the backstack to enable back navigation
            .commit()
    }
}