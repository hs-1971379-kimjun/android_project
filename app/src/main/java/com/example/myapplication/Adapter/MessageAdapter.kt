package com.example.myapplication.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class MessageItem(
    var sender: String? = null,  // sender 필드 추가
    var receiver: String? = null,  // receiver 필드 추가
    var msg: String? = null,  // msg 필드 추가
    var time: Long? = null,  // time 필드 추가
    var formattedTime: String? = null  // formattedTime 필드 추가
)

class MessageAdapter(val itemList: ArrayList<MessageItem>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.sender_message_layout, parent, false)
        return MessageViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messageItem = itemList[position]
        holder.bind(messageItem)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val msg: TextView = itemView.findViewById(R.id.messageTextView) ?: TextView(itemView.context)
        private val time: TextView = itemView.findViewById(R.id.timeTextView) ?: TextView(itemView.context)
        private val sender: TextView = itemView.findViewById(R.id.senderTextView) ?: TextView(itemView.context)

        fun bind(messageItem: MessageItem) {
            msg.text = messageItem.msg ?: "No"
            time.text = messageItem.time?.let { convertTimeToString(it) } ?: "No time"
            sender.text = messageItem.sender ?: "No sender"
        }
    }

    private fun convertTimeToString(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.add(Calendar.HOUR_OF_DAY, 9)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return simpleDateFormat.format(calendar.time)
    }
}
