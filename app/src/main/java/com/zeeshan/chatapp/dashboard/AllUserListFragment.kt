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
import com.google.firebase.firestore.*
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.adapter.UserListAdapter
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.utilities.AppPref

/**
 * A simple [Fragment] subclass.
 *
 */
class AllUserListFragment : Fragment() {

    private var userList = ArrayList<User>()
    private lateinit var userViewAdapter: UserListAdapter
    private lateinit var dbReference: FirebaseFirestore
    private lateinit var curUser: User
    lateinit var userData: ListenerRegistration


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_user_list, container, false)

        dbReference = FirebaseFirestore.getInstance()
        curUser = AppPref(activity!!).getUser()!!

        val recyclerView = view.findViewById<RecyclerView>(R.id.dashboardAllUserListRecycler)
        recyclerView.layoutManager = LinearLayoutManager(activity!!)

        userViewAdapter = UserListAdapter(activity!!, userList
            , {
                val chatIntent = Intent(activity, ChatActivity::class.java).apply {
                    //                putExtra("user",it)
                    ChatActivity.user = it
                }
                startActivity(chatIntent)
//                Toast.makeText(activity!!, "Clicked ${it.userEmail}", Toast.LENGTH_SHORT).show()
                userData.remove()
            },
            {
                Toast.makeText(activity!!, "Long Clicked ${it.userEmail}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = userViewAdapter
        fetchDataFromFirestore()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        dbReference = FirebaseFirestore.getInstance()
//        curUser = AppPref(activity!!).getUser()!!

//        Recycler
//        val recyclerView = view.findViewById<RecyclerView>(R.id.dashboardAllUserListRecycler)
//        recyclerView.layoutManager = LinearLayoutManager(activity!!)
//
//        userViewAdapter = UserListAdapter(activity!!, userList) {
//            val chatIntent = Intent(activity,ChatActivity::class.java).apply {
////                putExtra("user",it)
//                ChatActivity.user = it
//            }
//            startActivity(chatIntent)
//            //            Toast.makeText(activity!!, "Clicked ${it.userEmail}", Toast.LENGTH_SHORT).show()
//        }
//        recyclerView.adapter = userViewAdapter
//
//        fetchDataFromFirestore()
    }

    override fun onResume() {
        super.onResume()
//        userList.clear()
//        fetchDataFromFirestore()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
//        userList.clear()
//        fetchDataFromFirestore()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        userList.clear()
        userData = dbReference.collection("Users")
            .addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in querySnapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            if (dc.document.id != curUser.userId) {
                                userList.add(dc.document.toObject(User::class.java))
                                println(userList)
                                Log.d("UserListFragment", userList.toString())

                                userViewAdapter.notifyDataSetChanged()
                            }

                        }
                        DocumentChange.Type.MODIFIED -> {

                        }
                        DocumentChange.Type.REMOVED -> {

                        }
                    }
                }
            })
    }

    override fun onStop() {
        super.onStop()
        Log.v("UserFragment", "User List Fragment on Stop ")
//        making Array List null
//        userList.clear()
    }
}
