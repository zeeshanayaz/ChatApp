package com.zeeshan.chatapp.dashboard


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zeeshan.chatapp.R

/**
 * A simple [Fragment] subclass.
 *
 */
class RecentChatListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_chat_list, container, false)
    }


}
