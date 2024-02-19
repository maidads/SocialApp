package com.example.androidprojectma23

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        // Creating hardcoded examples
        chatMessages.add(ChatMessage("Användare 1", "Hej där!", "10:00", false))
        chatMessages.add(ChatMessage("Användare 2", "Hallå!", "10:01", false))

        chatAdapter = ChatAdapter(chatMessages, this)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
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