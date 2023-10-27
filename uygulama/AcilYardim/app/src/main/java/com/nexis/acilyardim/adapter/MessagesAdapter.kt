package com.nexis.acilyardim.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.nexis.acilyardim.databinding.MessagesItemBinding
import com.nexis.acilyardim.model.Channel
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.view.MainFragmentDirections

class MessagesAdapter(var channelList: ArrayList<Channel>, val userId: String) : RecyclerView.Adapter<MessagesAdapter.MessagesHolder>() {
    private lateinit var v: MessagesItemBinding
    private lateinit var navDirections: NavDirections
    private lateinit var mChannel: Channel
    private var aPos: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesHolder {
        v = MessagesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessagesHolder(v)
    }

    override fun onBindViewHolder(holder: MessagesHolder, position: Int) {
        mChannel = channelList[position]

        FirebaseUtil.getUserData(mChannel.userId) { userData ->
            holder.mI.user = userData
        }

        FirebaseUtil.getLastChat(mChannel.channelId, userId) { chatData ->
            if (chatData == null)
                holder.mI.messagesItemTxtLastMessages.text = "Mesaj geçmişi bulunamadı"
            else
                holder.mI.chat = chatData
        }

        holder.itemView.setOnClickListener {
            aPos = holder.adapterPosition

            if (aPos != RecyclerView.NO_POSITION)
                goToChatPage(userId, channelList[position].userId)
        }
    }

    override fun getItemCount(): Int = channelList.size

    inner class MessagesHolder(val mI: MessagesItemBinding) : RecyclerView.ViewHolder(mI.root)

    @SuppressLint("NotifyDataSetChanged")
    fun loadData(channels: ArrayList<Channel>){
        channelList = channels
        notifyDataSetChanged()
    }

    private fun goToChatPage(userId: String, targetUserId: String){
        FirebaseUtil.getUserData(targetUserId) { targetData ->
            FirebaseUtil.getUserData(userId) { userData ->
                if (targetData != null && userData != null){
                    navDirections = MainFragmentDirections.actionMainFragmentToChatFragment(userData, targetData)
                    Navigation.findNavController(v.root).navigate(navDirections)
                }
            }
        }
    }
}