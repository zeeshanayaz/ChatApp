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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_user_list, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbReference = FirebaseFirestore.getInstance()
        curUser = AppPref(activity!!).getUser()!!

//        Recycler
        val recyclerView = view.findViewById<RecyclerView>(R.id.dashboardAllUserListRecycler)
        recyclerView.layoutManager = LinearLayoutManager(activity!!)

        userViewAdapter = UserListAdapter(activity!!, userList) {
            val chatIntent = Intent(activity,ChatActivity::class.java).apply {
                putExtra("user",it)
            }
            startActivity(chatIntent)
            //            Toast.makeText(activity!!, "Clicked ${it.userEmail}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = userViewAdapter

        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        dbReference.collection("Users")
            .addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in querySnapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            if (dc.document.id != curUser.userId)
                            {
                                userList.add(dc.document.toObject(User::class.java))
                                println(userList)
                                Log.d("UserListFragment",userList.toString())
                            }
//                            Log.d(TAG, "New city: ${dc.document.data}")

                        }
                        DocumentChange.Type.MODIFIED -> {

                        }
                        DocumentChange.Type.REMOVED -> {

                        }
                    }
                }
                userViewAdapter.notifyDataSetChanged()
//                querySnapshot?.documents?.forEach {
//                    if (it.id != curUser.userId) {
//                        userList.add(it.toObject(User::class.java)!!)
//                        println(userList)
//                        userViewAdapter.notifyDataSetChanged()
////                        if (userListEmptyCheck.visibility == View.VISIBLE) userListEmptyCheck.visibility = View.GONE
//                    }
//                }
//                for (eachUser in snapshots!!){
//                    if (eachUser != null)
//                    {
//                        println(eachUser.toObject(User::class.java))
//                        userList.add(eachUser.toObject(User::class.java))
//                        userViewAdapter.notifyDataSetChanged()
//                    }
//                }

            })
    }

    override fun onStop() {
        super.onStop()
        Log.v("UserFragment", "User List Fragment on Stop ")
//        making Array List null
        userList.clear()
    }
}
