package com.example.myapplication.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.MessageAdapter
import com.example.myapplication.Adapter.MessageItem
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowChatActivity : AppCompatActivity() {
    private val messages = ArrayList<MessageItem>()
    private lateinit var messageStorageRef: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_received_messages)

        setupRecyclerView()

        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        messageStorageRef = FirebaseDatabase.getInstance().getReference("Message")

        messageStorageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                parseSnapshotData(snapshot, currentUserEmail)
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                handleErrorMessage()
            }
        })
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.rc_message)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(messages)
        recyclerView.adapter = messageAdapter
    }

    private fun parseSnapshotData(snapshot: DataSnapshot, currentUserEmail: String?) {
        val messages = mutableListOf<MessageItem>()

        snapshot.children.forEach { snapshotChild ->
            val senderId: String? = snapshotChild.child("sender").getValue(String::class.java)
            val receiverId: String? = snapshotChild.child("receiver").getValue(String::class.java)
            val messageText: String? = snapshotChild.child("msg").getValue(String::class.java)
            val time: Long? = snapshotChild.child("time").getValue(Long::class.java)

            val messageItem = MessageItem(senderId, receiverId, messageText, time, null)
            if (receiverId == currentUserEmail) {
                messages.add(messageItem)
            }
        }
    }

    private fun handleErrorMessage() {
        Toast.makeText(this@ShowChatActivity, "메시지 출력 실패", Toast.LENGTH_SHORT).show()
    }
}

