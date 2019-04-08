package com.zeeshan.chatapp.dashboard


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.adapter.UserListAdapter
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.utilities.AppPref

/**
 * A simple [Fragment] subclass.
 *
 */
class RecentChatListFragment : Fragment() {

    private var recentChatUser = ArrayList<User>()
    private lateinit var userViewAdapter: UserListAdapter
    private lateinit var dbReference: FirebaseFirestore
    private lateinit var curUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recent_chat_list, container, false)

        dbReference = FirebaseFirestore.getInstance()
        curUser = AppPref(activity!!).getUser()!!

        val recyclerView = view.findViewById<RecyclerView>(R.id.dashboardRecentChatListRecycler)
        recyclerView.layoutManager = LinearLayoutManager(activity!!)

        userViewAdapter = UserListAdapter(activity!!, recentChatUser
            , {
                val chatIntent = Intent(activity, ChatActivity::class.java).apply {
                    //                putExtra("user",it)
                    ChatActivity.user = it
                }
                startActivity(chatIntent)
//                Toast.makeText(activity!!, "Clicked ${it.userEmail}", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText(activity!!, "Long Clicked ${it.userEmail}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = userViewAdapter
        fetchDataFromFirestore()


        return view
    }

    private fun fetchDataFromFirestore() {
        recentChatUser.clear()

        dbReference.collection("Chats").document("User-Chat")
            .addSnapshotListener(EventListener<DocumentSnapshot>{
                documentSnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

            })

    }
}
