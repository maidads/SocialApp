package com.example.androidprojectma23

import android.content.Context
import android.os.Bundle
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
        val conversationId = arguments?.getString("conversationId").toString()
        val conversationProfileImageUrl = arguments?.getString("conversationProfileImageUrl").toString()
        val conversationUserName = arguments?.getString("conversationUserName").toString()

        // Initial setup for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ChatConversationAdapter(chatMessages)
        recyclerView.adapter = adapter

        myInfo(object : UserInfoCallback {
            override fun onUserInfoReceived(userName: String, profileImageUrl: String) {
                // When user info is loaded, run getConversation()
                getConversation(conversationId, conversationProfileImageUrl, conversationUserName)
            }
        })

        sendButton.setOnClickListener {
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
                    .addOnSuccessListener { documentReference ->
                        Log.d("!!!", "Meddelande sparades med ID: ${documentReference.id}")

                        hideKeyboard()

                        //adapter.notifyItemInserted(chatMessages.size - 1)
                        //recyclerView.scrollToPosition(chatMessages.size - 1)
                    }
                    .addOnFailureListener { e ->
                        Log.w("!!!", "Fel vid sparande av meddelande", e)
                    }
                messageInput.text.clear()
            }
        }
        return view
    }

    private fun getConversation(conversationId: String, conversationProfileImageUrl: String, conversationUserName: String){
        val db = FirebaseFirestore.getInstance()

        db.collection("conversations").document(conversationId).collection("messages")
            .orderBy("messageTime")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("!!!", "Error getting documents: ", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = mutableListOf<ChatMessage>()

                    for (document in snapshot.documents) {
                        val senderUser = document.getString("userName")

                        if (senderUser != currentUser) {
                            userName = conversationUserName
                            profileImageUrl = conversationProfileImageUrl
                            isUserMessage = false
                        } else {
                            userName = currentUserName
                            profileImageUrl = currentUserProfileImage
                            isUserMessage = true
                        }

                        val messageText = document.getString("messageBody")
                        val messageTime = document.getTimestamp("messageTime")

                        if (messageText != null && messageTime != null) {
                            val chatMessage = ChatMessage(
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

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun myInfo(callback: UserInfoCallback) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currentUser)
            .get()
            .addOnSuccessListener {
                currentUserName = it.getString("displayName")!!
                currentUserProfileImage = it.getString("profileImageUrl")!!
                callback.onUserInfoReceived(currentUserName, currentUserProfileImage)
            }
    }

    private fun updateRecyclerView(messages: MutableList<ChatMessage>) {
        chatMessages.clear()
        chatMessages.addAll(messages)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}

interface UserInfoCallback {
    fun onUserInfoReceived(userName: String, profileImageUrl: String)
}

