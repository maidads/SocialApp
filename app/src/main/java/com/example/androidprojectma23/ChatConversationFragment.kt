package com.example.androidprojectma23

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ChatConversationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: FloatingActionButton
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var currentUserName: String
    private lateinit var currentUserProfileImage: String
    private var isUserMessage = true
    private lateinit var profileImageUrl: String
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
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
        val conversationUserId = arguments?.getString("conversationUserId").toString()
        val conversationProfileImageUrl =
            arguments?.getString("conversationProfileImageUrl").toString()
        val conversationUserName = arguments?.getString("conversationUserName").toString()

        setTopBarTitle(conversationUserName)

        // Initial setup for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ChatConversationAdapter(chatMessages)
        recyclerView.adapter = adapter

        myInfo(object : UserInfoCallback {
            override fun onUserInfoReceived(userName: String, profileImageUrl: String) {
                var existingConversationId: String? = null

                findConversationId(currentUser, conversationUserId) { foundConversationId ->
                    existingConversationId = foundConversationId

                    if (existingConversationId != null) {

                        getConversation(
                            conversationUserId,
                            existingConversationId!!,
                            conversationProfileImageUrl,
                            conversationUserName
                        )
                        sendButton.setOnClickListener {
                            saveAndSendMessage(existingConversationId!!)
                        }
                    } else {

                        setUpNewConversation(conversationUserId, currentUser) { newConversationId ->
                            saveAndSendMessage(newConversationId)
                            getConversation(
                                conversationUserId,
                                newConversationId,
                                conversationProfileImageUrl,
                                conversationUserName
                            )

                            existingConversationId = newConversationId
                            sendButton.setOnClickListener {
                                saveAndSendMessage(newConversationId)
                            }
                        }
                    }
                }
            }
        })
        return view
    }

    private fun setUpNewConversation(
        conversationUserId: String,
        currentUser: String,
        callback: (String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val conversationsCollection = db.collection("conversations")
        val newConversationDocument = conversationsCollection.document()
        val newConversationId = newConversationDocument.id
        newConversationDocument.collection("messages")

        val userIds = listOf(conversationUserId, currentUser)
        newConversationDocument.set(
            mapOf(
                "userID1" to currentUser,
                "userID2" to conversationUserId
            )
        )

        for (userId in userIds) {
            val userDocument = db.collection("users").document(userId)
            userDocument.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userConversations = snapshot.get("userConversations") as? List<String>
                    val updatedConversations =
                        userConversations?.plus(newConversationId) ?: listOf(newConversationId)
                    userDocument.update("userConversations", updatedConversations)
                } else {
                    userDocument.set(mapOf("userConversations" to listOf(newConversationId)))
                }
            }
        }

        callback(newConversationId)
    }

    private fun findConversationId(user1: String, user2: String, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val conversationsCollection = db.collection("conversations")

        val search1 =
            conversationsCollection.whereEqualTo("userID1", user1).whereEqualTo("userID2", user2)
        val search2 =
            conversationsCollection.whereEqualTo("userID1", user2).whereEqualTo("userID2", user1)

        search1.get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                callback(snapshot.documents[0].id)
            } else {
                search2.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        callback(snapshot.documents[0].id)
                    } else {
                        callback(null)
                    }
                }
            }
        }
    }

    private fun saveAndSendMessage(conversationId: String) {
        val messageText = messageInput.text.toString()
        if (messageText.isNotEmpty()) {
            val timestamp = FieldValue.serverTimestamp()
            val message = hashMapOf(
                "messageBody" to messageText,
                "messageTime" to timestamp,
                "userName" to currentUser,
            )

            val db = FirebaseFirestore.getInstance()

            db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    hideKeyboard()
                }
                .addOnFailureListener {
                }
            messageInput.text.clear()
        }
    }

    private fun getConversation(
        conversationUserId: String,
        conversationId: String,
        conversationProfileImageUrl: String,
        conversationUserName: String
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("conversations").document(conversationId).collection("messages")
            .orderBy("messageTime")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = mutableListOf<ChatMessage>()

                    for (document in snapshot.documents) {
                        val senderUser = document.getString("userName")

                        if (senderUser != currentUser) {
                            userId = conversationUserId
                            userName = conversationUserName
                            profileImageUrl = conversationProfileImageUrl
                            isUserMessage = false
                        } else {
                            userId = currentUser
                            userName = currentUserName
                            profileImageUrl = currentUserProfileImage
                            isUserMessage = true
                        }

                        val messageText = document.getString("messageBody")
                        val messageTime = document.getTimestamp("messageTime")

                        if (messageText != null && messageTime != null) {
                            val chatMessage = ChatMessage(
                                userId,
                                userName,
                                messageText,
                                messageTime,
                                profileImageUrl,
                                isUserMessage
                            )
                            messages.add(chatMessage)
                        }
                    }
                    updateRecyclerView(messages)
                    messages.clear()
                }
            }
    }

    private fun setTopBarTitle(conversationUserName: String) {
        val topBarActivity = (activity as LandingPageActivity)
        topBarActivity.setTitle(conversationUserName)
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun myInfo(callback: UserInfoCallback) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currentUser)
            .get()
            .addOnSuccessListener {
                val defaultProfileImageUrl =
                    "https://firebasestorage.googleapis.com/v0/b/androidprojectma23-c28c0.appspot.com/o/userImages%2Fchat_image_placeholder.png?alt=media&token=e5c0b2ed-6518-4607-a1af-ce5b8e2e8e81" // Ange URL till din standardprofilbild här
                currentUserName = it.getString("displayName") ?: "Unknown User"
                currentUserProfileImage = it.getString("profileImageUrl") ?: defaultProfileImageUrl
                callback.onUserInfoReceived(currentUserName, currentUserProfileImage)
            }
    }

    private fun updateRecyclerView(messages: MutableList<ChatMessage>) {
        chatMessages.clear()
        chatMessages.addAll(messages)
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scrollToPosition(chatMessages.size - 1)
    }
}

interface UserInfoCallback {
    fun onUserInfoReceived(userName: String, profileImageUrl: String)
}

