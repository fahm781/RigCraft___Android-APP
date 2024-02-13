package com.fahm781.rigcraft.ChatbotServices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.R

class MsgAdapter(private val msgList: List<Msg>) :
    RecyclerView.Adapter<MsgAdapter.MsgViewHolder>() {

    class MsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftChatView: LinearLayout = itemView.findViewById(R.id.left_chat_view)
        val rightChatView: LinearLayout = itemView.findViewById(R.id.right_chat_view)
        val leftMsg: TextView = itemView.findViewById(R.id.left_chat_text_view)
        val rightMsg: TextView = itemView.findViewById(R.id.right_chat_text_view)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_view, null)
        //if the code below doesnt work, uncomment the code above
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_view, parent, false)
        return MsgViewHolder(view)
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val message = msgList[position]
        if (message.role == MY_MSG) {
            holder.rightChatView.visibility = View.VISIBLE
            holder.leftChatView.visibility = View.GONE
            holder.rightMsg.text = message.content
        }
        if(message.role == BOT_MSG) {
            holder.leftChatView.visibility = View.VISIBLE
            holder.rightChatView.visibility = View.GONE
            holder.leftMsg.text = message.content
        }
    }

    override fun getItemCount(): Int = msgList.size
}



