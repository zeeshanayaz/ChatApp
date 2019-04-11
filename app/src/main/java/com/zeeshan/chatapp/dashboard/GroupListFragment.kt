package com.zeeshan.chatapp.dashboard


import android.content.ContentValues
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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.zeeshan.chatapp.GroupChatActivity
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.adapter.GroupListAdapter
import com.zeeshan.chatapp.model.GroupChat
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.utilities.AppPref

class GroupListFragment : Fragment() {

    private lateinit var dbReference: FirebaseFirestore
    private lateinit var groupViewAdapter: GroupListAdapter
    private lateinit var curUser: User
    private var groupList = ArrayList<GroupChat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group_list, container, false)
        groupList.clear()
        dbReference = FirebaseFirestore.getInstance()
        curUser = AppPref(activity!!).getUser()!!

        val recyclerView = view.findViewById<RecyclerView>(R.id.dashboardGroupListRecycler)
        recyclerView.layoutManager = LinearLayoutManager(activity!!)

        groupViewAdapter = GroupListAdapter(activity!!, groupList
            , {
                val chatIntent = Intent(activity, GroupChatActivity::class.java).apply {
//                putExtra("user",it)
                    GroupChatActivity.groupChat = it
                }
                startActivity(chatIntent)
//                Toast.makeText(activity!!, "Clicked ${it.groupName}", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText(activity!!, "Long Clicked ${it.groupName}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = groupViewAdapter

        retrieveGroupListFromFirestore()
        return view
    }

    private fun retrieveGroupListFromFirestore() {
        dbReference.collection("Groups")
            .addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w(ContentValues.TAG, "listen:error", firebaseFirestoreException)
                    return@EventListener
                }
                for (dc in querySnapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val groupData = dc.document.toObject(GroupChat::class.java)
                            if (groupData.groupMember!!.contains(curUser.userId)){
                                groupList.add(groupData)
                            }
//                            groupList.add(dc.document.toObject(GroupChat::class.java))
//                            println(groupList)
                            Log.d("GroupListFragment", groupList.toString())

                            groupViewAdapter.notifyDataSetChanged()
                        }
                        DocumentChange.Type.MODIFIED -> {

                        }
                        DocumentChange.Type.REMOVED -> {

                        }

                    }
                }
            })
    }

}
