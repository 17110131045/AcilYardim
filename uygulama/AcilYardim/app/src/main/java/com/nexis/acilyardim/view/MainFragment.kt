package com.nexis.acilyardim.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.nexis.acilyardim.R
import com.nexis.acilyardim.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var v: View
    private lateinit var mainBinding: FragmentMainBinding
    private lateinit var transaction: FragmentTransaction

    private lateinit var userId: String
    private lateinit var fragmentList: Array<Fragment>

    private fun init(){
        arguments?.let {
            userId = MainFragmentArgs.fromBundle(it).userId
            fragmentList = arrayOf(HomeFragment(userId), MessagesFragment(userId), ProfileFragment(userId))
            setFragment(fragmentList[0])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = FragmentMainBinding.inflate(inflater, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()

        mainBinding.mainFragmentBottomNav.setOnItemSelectedListener {
            when (it.itemId){
                R.id.bottom_menu_home -> {
                    setFragment(fragmentList[0])
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_menu_messages -> {
                    setFragment(fragmentList[1])
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_menu_profile -> {
                    setFragment(fragmentList[2])
                    return@setOnItemSelectedListener true
                }

                else -> return@setOnItemSelectedListener true
            }
        }
    }

    private fun setFragment(fragment: Fragment){
        transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment_frameLayout, fragment)
        transaction.commit()
    }
}