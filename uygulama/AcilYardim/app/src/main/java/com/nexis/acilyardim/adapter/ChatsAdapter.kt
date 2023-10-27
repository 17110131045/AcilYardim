package com.nexis.acilyardim.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nexis.acilyardim.databinding.ChatItemBinding
import com.nexis.acilyardim.model.Chat
import com.nexis.acilyardim.util.FirebaseUtil

class ChatsAdapter(var chatList: ArrayList<Chat>, val userId: String) : RecyclerView.Adapter<ChatsAdapter.ChatsHolder>() {
    private lateinit var v: ChatItemBinding
    private lateinit var mChat: Chat

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        v = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatsHolder(v)
    }

    override fun onBindViewHolder(holder: ChatsHolder, position: Int) {
        mChat = chatList[position]

        if (mChat.senderId == userId){
            holder.cI.chatItemRelativeRight.visibility = View.VISIBLE
            holder.cI.chatItemRelativeLeft.visibility = View.GONE
            holder.cI.chatItemTxtRightContent.visibility = View.VISIBLE
            holder.cI.chatright = mChat

            FirebaseUtil.getUserData(mChat.senderId) { userData ->
                holder.cI.userright = userData
            }
        } else {
            holder.cI.chatItemRelativeLeft.visibility = View.VISIBLE
            holder.cI.chatItemRelativeRight.visibility = View.GONE
            holder.cI.chatItemTxtLeftContent.visibility = View.VISIBLE
            holder.cI.chatleft = mChat

            FirebaseUtil.getUserData(mChat.senderId) { userData ->
                holder.cI.userleft = userData
            }
        }

        /*holder.cI.chatItemImgRightImg.setOnClickListener {
            aPos = holder.adapterPosition

            if (aPos != RecyclerView.NO_POSITION)
                AppUtil.showFullScreenImageDialog(v.root, chatList[aPos].messageContent)
        }

        holder.cI.chatItemImgLeftImg.setOnClickListener {
            aPos = holder.adapterPosition

            if (aPos != RecyclerView.NO_POSITION)
                AppUtil.showFullScreenImageDialog(v.root, chatList[aPos].messageContent)
        }*/
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatsHolder(val cI: ChatItemBinding) : RecyclerView.ViewHolder(cI.root)

    @SuppressLint("NotifyDataSetChanged")
    fun loadData(chats: ArrayList<Chat>){
        chatList = chats
        notifyDataSetChanged()
    }
}