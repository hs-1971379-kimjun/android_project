//package com.example.myapplication.Adapter
//
//import android.view.LayoutInflater
//import android.view.View
//
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.myapplication.R
//
//data class chatItem(
//    val msg: String? = null,
//    val time: Long? = null,
//    var timeString: String? = null,
//    val sender: String? = null,
//    val receiver: String? = null
//)
//
//class ChatAdapter (val itemList : ArrayList<chatItem>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_message_display, parent, false)
//        return ChatViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.count()
//    }
//
//    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
//        holder.msg.text = itemList[position].msg ?: "No"
//        holder.time.text = itemList[position].timeString ?: "No time available"
//    }
//
//    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val msg: TextView = itemView.findViewById(R.id.chatText) ?: TextView(itemView.context)
//        val time: TextView = itemView.findViewById(R.id.time) ?: TextView(itemView.context)
//    }
//}

package com.example.myapplication.Adapter

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMessageDisplayBinding

//data class chatItem(
//    val content: String? = null,
//    val timestamp: Long? = null,
//    var formattedTime: String? = null,
//    val senderUsername: String? = null,
//    val receiverUsername: String? = null
//)
data class chatItem(
    var sender: String? = null,  // sender 필드 추가
    var receiver: String? = null,  // receiver 필드 추가
    var msg: String? = null,  // msg 필드 추가
    var time: Long? = null,  // time 필드 추가
    var formattedTime: String? = null  // formattedTime 필드 추가
)

class ChatAdapter(private val itemList: List<chatItem>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ActivityMessageDisplayBinding.inflate(inflater, parent, false)
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ChatViewHolder(private val binding: ActivityMessageDisplayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: chatItem) {
            binding.chatText.text = chatItem.msg ?: "No"
            binding.time.text = chatItem.formattedTime ?: "No time available"
        }
    }
}