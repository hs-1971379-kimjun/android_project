package com.example.myapplication.Activity

import com.example.myapplication.R

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.ChatAdapter
import com.example.myapplication.Adapter.chatItem

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatScreenActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var editChatting: EditText
    private lateinit var StorageUserRef: DatabaseReference
    private lateinit var StorageMsgRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatItemList = ArrayList<chatItem>()
    private lateinit var sender: String
    private lateinit var receiver: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)

        setupUI()
        setupRecyclerView()

        StorageMsgRef = FirebaseDatabase.getInstance().reference.child("Message")
        observeMessages()

        sendButton.setOnClickListener {
            val message = editChatting.text.toString()
            sendMessage(message)
            editChatting.text.clear()
        }
    }

    private fun setupUI() {
        sender = FirebaseAuth.getInstance().currentUser?.email.toString()
        receiver = intent.getStringExtra("userEmail").toString()

        val sendEmail = findViewById<TextView>(R.id.sendEmail)
        sendEmail.text = "$receiver 님과의 대화방"

        editChatting = findViewById(R.id.chatting)
        sendButton = findViewById(R.id.sendButton)
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.chat_rv)
        recyclerView.visibility = View.GONE
        chatAdapter = ChatAdapter(chatItemList)

        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeMessages() {
        StorageMsgRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatItemList.clear()

                for (messageSnapshot in snapshot.children) {
                    val messageData = messageSnapshot.getValue(chatItem::class.java)

                    if (messageData != null && isMatchingSenderReceiver(messageData)) {
                        formatAndAddMessage(messageData)
                    } else {
                    }
                }

                updateUIAfterMessagesLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun isMatchingSenderReceiver(messageData: chatItem): Boolean {
        return messageData.sender == sender && messageData.receiver == receiver
    }

    private fun formatAndAddMessage(messageData: chatItem) {
        val timestamp = messageData.time ?: 0
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.add(Calendar.HOUR_OF_DAY, 9)

        val simpleDateFormat = SimpleDateFormat("H:mm", Locale.getDefault())
        val formattedTime = simpleDateFormat.format(calendar.time)

        messageData.formattedTime = formattedTime
        chatItemList.add(messageData)
    }

    private fun updateUIAfterMessagesLoaded() {
        chatAdapter.notifyDataSetChanged()
        if (chatAdapter.itemCount > 1) {
            recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }
        recyclerView.visibility = View.VISIBLE
    }


    private fun sendMessage(msg: String) {
        StorageUserRef = FirebaseDatabase.getInstance().getReference("Items")
        StorageMsgRef = FirebaseDatabase.getInstance().getReference("Message")

        val messageData = hashMapOf(
            "sender" to sender,
            "receiver" to receiver,
            "msg" to msg,
            "time" to ServerValue.TIMESTAMP
        )

        val newMessageRef = StorageMsgRef.push()
        newMessageRef.setValue(messageData)
    }
}