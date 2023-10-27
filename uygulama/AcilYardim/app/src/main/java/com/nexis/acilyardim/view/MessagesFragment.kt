package com.nexis.acilyardim.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexis.acilyardim.R
import com.nexis.acilyardim.adapter.MessagesAdapter
import com.nexis.acilyardim.adapter.decoration.LinearVerticalDecoration
import com.nexis.acilyardim.databinding.FragmentMessagesBinding
import com.nexis.acilyardim.model.Channel
import com.nexis.acilyardim.util.Singleton
import com.nexis.acilyardim.util.show
import com.nexis.acilyardim.viewmodel.MessagesViewModel

class MessagesFragment(val userId: String) : Fragment() {
    private lateinit var v: View
    private lateinit var messagesBinding: FragmentMessagesBinding
    private lateinit var messagesViewModel: MessagesViewModel

    private lateinit var channelList: ArrayList<Channel>
    private lateinit var messagesAdapter: MessagesAdapter

    private fun init(){
        messagesBinding.messagesFragmentRecyclerView.setHasFixedSize(true)
        messagesBinding.messagesFragmentRecyclerView.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
        messagesAdapter = MessagesAdapter(arrayListOf(), userId)
        messagesBinding.messagesFragmentRecyclerView.adapter = messagesAdapter

        messagesViewModel = ViewModelProvider(this)[MessagesViewModel::class.java]
        observeLiveData()
        messagesViewModel.getChannels(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        messagesBinding = FragmentMessagesBinding.inflate(inflater, container, false)
        return messagesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        messagesViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        messagesViewModel.successMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
                messagesAdapter.loadData(arrayListOf())
                messagesViewModel.getChannels(userId)
            }
        })

        messagesViewModel.channelList.observe(viewLifecycleOwner, Observer {
            it?.let {
                channelList = it

                if (messagesBinding.messagesFragmentRecyclerView.itemDecorationCount > 0)
                    messagesBinding.messagesFragmentRecyclerView.removeItemDecorationAt(0)

                messagesBinding.messagesFragmentRecyclerView.addItemDecoration(LinearVerticalDecoration(Singleton.V_SIZE))
                messagesAdapter.loadData(it)
            }
        })
    }
}