package com.example.myapplication.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMessageDisplayBinding

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