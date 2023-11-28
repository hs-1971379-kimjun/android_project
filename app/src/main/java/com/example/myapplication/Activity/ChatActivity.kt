package com.example.myapplication.Activity

import com.example.myapplication.R

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.ChatAdapter
import com.example.myapplication.Adapter.chatItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var editChatting: EditText
    private lateinit var userStorageRef: DatabaseReference
    private lateinit var msgStorageRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val itemList = ArrayList<chatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sender = FirebaseAuth.getInstance().currentUser?.email.toString()
        val receiver = intent.getStringExtra("userEmail")
        val sendEmail = findViewById<TextView>(R.id.sendEmail)
        sendEmail.text = receiver.toString() + "님과의 대화방"


        editChatting = findViewById<EditText>(R.id.chatting)
        sendButton = findViewById<Button>(R.id.sendButton)
        recyclerView = findViewById<RecyclerView>(R.id.chat_rv)
        recyclerView.visibility = View.GONE
        chatAdapter = ChatAdapter(itemList)


        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        msgStorageRef = FirebaseDatabase.getInstance().reference.child("Message")
        msgStorageRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()

                for (itemSnap in snapshot.children) {
                    val messageData = itemSnap.getValue(chatItem::class.java)

                    if (messageData != null) {
                        if (messageData.sender == sender && messageData.receiver == receiver) {

                            val timestamp = messageData.time ?: 0
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = timestamp

                            calendar.add(Calendar.HOUR_OF_DAY, 9)

                            val simpleDateFormat = SimpleDateFormat("H:mm", Locale.getDefault())
                            val formattedTime = simpleDateFormat.format(calendar.time)

                            messageData.timeString = formattedTime
                            itemList.add(messageData)
                        }
                    } else {
                        Toast.makeText(this@ChatActivity, "메시지 전송 오류", Toast.LENGTH_SHORT).show()
                    }
                }
                chatAdapter.notifyDataSetChanged()
                if(chatAdapter.itemCount > 1)
                    recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "메시지 전송 실패", Toast.LENGTH_SHORT).show()
            }
        })


        sendButton.setOnClickListener {
            val msg = editChatting.text.toString()
            sendmsg(sender, receiver, msg)

            editChatting.text.clear()
        }
    }

    private fun sendmsg(sender: String?, receiver: String?, msg: String?) {
        userStorageRef = FirebaseDatabase.getInstance().getReference("Items")
        msgStorageRef = FirebaseDatabase.getInstance().getReference("Message")

        val messageData = hashMapOf(
            "sender" to sender,
            "receiver" to receiver,
            "msg" to msg,
            "time" to ServerValue.TIMESTAMP
        )

        
        val newMessageRef = msgStorageRef.push()
        newMessageRef.setValue(messageData)
            .addOnSuccessListener {
                val messageId = newMessageRef.key
                Toast.makeText(this, "메시지 전송 성공", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "메시지 전송 실패", Toast.LENGTH_SHORT).show()
            }
    }
}