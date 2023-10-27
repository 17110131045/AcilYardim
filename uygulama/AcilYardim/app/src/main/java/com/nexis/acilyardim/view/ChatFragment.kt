package com.nexis.acilyardim.view

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexis.acilyardim.R
import com.nexis.acilyardim.adapter.ChatsAdapter
import com.nexis.acilyardim.adapter.decoration.LinearVerticalDecoration
import com.nexis.acilyardim.databinding.FragmentChatBinding
import com.nexis.acilyardim.model.Chat
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.util.Singleton
import com.nexis.acilyardim.util.show
import com.nexis.acilyardim.viewmodel.ChatViewModel
import java.io.ByteArrayOutputStream

class ChatFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var chatBinding: FragmentChatBinding
    private lateinit var chatViewModel: ChatViewModel

    private lateinit var txtMessage: String
    private lateinit var chatList: ArrayList<Chat>
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var userData: User
    private lateinit var targetData: User
    private var channelId: String? = null

    private fun init(){
        arguments?.let {
            userData = ChatFragmentArgs.fromBundle(it).userData
            targetData = ChatFragmentArgs.fromBundle(it).targetData
            chatBinding.targetuser = targetData

            chatBinding.chatFragmentRecyclerView.setHasFixedSize(true)
            chatBinding.chatFragmentRecyclerView.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
            chatsAdapter = ChatsAdapter(arrayListOf(), userData.userId)
            chatBinding.chatFragmentRecyclerView.adapter = chatsAdapter

            chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
            observeLiveData()
            chatViewModel.checkChannel(userData.userId, targetData.userId)

            chatBinding.chatFragmentImgSend.setOnClickListener(this)
            chatBinding.chatFragmentImgClose.setOnClickListener(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatBinding = FragmentChatBinding.inflate(inflater, container, false)
        return chatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        chatViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        chatViewModel.channelId.observe(viewLifecycleOwner, Observer {
            if (it != null){
                channelId = it
                chatViewModel.getChats(channelId!!, userData.userId)
            } else
                chatViewModel.createChannel(userData.userId, targetData.userId)
        })

        chatViewModel.sendedState.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it)
                    chatBinding.chatFragmentEditMessage.setText("")
            }
        })

        chatViewModel.chatList.observe(viewLifecycleOwner, Observer {
            it?.let {
                chatList = it

                if (chatBinding.chatFragmentRecyclerView.itemDecorationCount > 0)
                    chatBinding.chatFragmentRecyclerView.removeItemDecorationAt(0)

                chatBinding.chatFragmentRecyclerView.addItemDecoration(LinearVerticalDecoration(Singleton.V_SIZE))
                chatsAdapter.loadData(it)
                chatBinding.chatFragmentRecyclerView.scrollToPosition((chatsAdapter.itemCount - 1))
            }
        })
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.chat_fragment_imgSend -> sendMessage()
                R.id.chat_fragment_imgClose -> backToPage()
            }
        }
    }

    private fun sendMessage(){
        channelId?.let {
            txtMessage = chatBinding.chatFragmentEditMessage.text.toString().trim()

            if (txtMessage.isEmpty()){
                "message".show(v, "Mesaj göndermek için bir şeyler yazmalısınız")
                return
            }

            chatViewModel.sendMessage(txtMessage, userData.userId, userData.userName, targetData.userId, channelId!!, "text")
        }
    }

    private fun backToPage(){
        Navigation.findNavController(v).popBackStack()
    }
}