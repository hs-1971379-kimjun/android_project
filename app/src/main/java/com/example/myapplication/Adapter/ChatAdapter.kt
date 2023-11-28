package com.example.myapplication.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import org.w3c.dom.Text

data class chatItem(
    val msg: String? = null,
    val time: Long? = null,
    var timeString: String? = null,
    val sender: String? = null,
    val receiver: String? = null
)

class ChatAdapter (val itemList : ArrayList<chatItem>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.msg.text = itemList[position].msg ?: "No"
        holder.time.text = itemList[position].timeString ?: "No time available"
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.chatText) ?: TextView(itemView.context)
        val time: TextView = itemView.findViewById(R.id.time) ?: TextView(itemView.context)
    }
}