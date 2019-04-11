package com.zeeshan.chatapp

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.zeeshan.chatapp.adapter.UserChatListAdapter
import com.zeeshan.chatapp.adapter.UserListAdapter
import com.zeeshan.chatapp.model.ChatMessage
import com.zeeshan.chatapp.model.GroupChat
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.utilities.AppPref
import kotlinx.android.synthetic.main.activity_group_chat.*
import kotlinx.android.synthetic.main.card_user.view.*
import kotlinx.android.synthetic.main.group_add_member_dialog.view.*

class GroupChatActivity : AppCompatActivity() {

    companion object {
        var groupChat: GroupChat? = null
    }

    private lateinit var groupChatAdapter: UserChatListAdapter
    var groupChatList: ArrayList<ChatMessage> = ArrayList()
    var groupChat: GroupChat? = null
    private lateinit var dbReference: FirebaseFirestore
    lateinit var currUser: User
    private lateinit var userViewAdapter: UserListAdapter
    private var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        dbReference = FirebaseFirestore.getInstance()

        if (intent != null) {
            groupChat = GroupChatActivity.groupChat
        } else {
            finish()
        }

        groupChatMessageListRecycler.layoutManager = LinearLayoutManager(this@GroupChatActivity)
        groupChatAdapter = UserChatListAdapter(this@GroupChatActivity, groupChatList)
        groupChatMessageListRecycler.adapter = groupChatAdapter

        retrieveMessages(groupChat!!.groupId)

        sendMessageButton.setOnClickListener { message ->
            if (!messageTextField.text.trim().isEmpty()) {
                currUser = AppPref(this@GroupChatActivity).getUser()!!

                val chatID = groupChat!!.groupId

                val msgID = System.currentTimeMillis().toString()

                val chatMessage = ChatMessage(
                    chatID,                                         //Chat ID  =  Group ID
                    messageTextField.text.toString().trim(),
                    System.currentTimeMillis(),
                    msgID,                                          //Msg ID
                    currUser.userId,                                //Sender ID
                    groupChat!!.groupId                             //Reciever Group
                )

                dbReference.collection("Chats").document("Group-Chat").collection(groupChat!!.groupId).document(msgID)
                    .set(chatMessage)
                    .addOnSuccessListener {
                        Log.d("GroupChatActivity", "DocumentSnapshot/Message successfully Send!")
                    }
                    .addOnFailureListener {
                        Log.w("GroupChatActivity", "Error sending Message", it)
                    }


                messageTextField.setText("")


            }
        }
    }


    private fun retrieveMessages(groupId: String) {


        dbReference.collection("Chats").document("Group-Chat").collection(groupId)
            .addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in querySnapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            groupChatList.add(dc.document.toObject(ChatMessage::class.java))
                            println(groupChatList)
                            Log.d("UserListFragment", groupChatList.toString())
//                            Log.d(TAG, "New city: ${dc.document.data}")

                        }
                        DocumentChange.Type.MODIFIED -> {

                        }
                        DocumentChange.Type.REMOVED -> {

                        }
                    }
                }
                groupChatMessageListRecycler.scrollToPosition(groupChatList.size - 1)
                groupChatAdapter.notifyDataSetChanged()
            })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.group_chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menu_add_member -> {
                createAddMemberDialog()
//                Toast.makeText(this@GroupChatActivity, getString(R.string.beta_version), Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.menu_exit_group -> {
                Toast.makeText(this@GroupChatActivity, getString(R.string.beta_version), Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createAddMemberDialog() {


        val groupMemberDialog =
            LayoutInflater.from(this@GroupChatActivity).inflate(R.layout.group_add_member_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this@GroupChatActivity)
            .setView(groupMemberDialog)
            .setTitle("Select Members..")
            .show()

//        val recyclerView = view.findViewById<RecyclerView>(R.id.dashboardAllUserListRecycler)
        groupMemberDialog.groupAllUserListRecycler!!.layoutManager = LinearLayoutManager(this@GroupChatActivity)
        userViewAdapter = UserListAdapter(this, userList
            , {
                Toast.makeText(this, "${it.userName} added to the list", Toast.LENGTH_SHORT).show()
                groupMemberDialog.profileSelectIcon.visibility = View.VISIBLE
                if (!groupChat!!.groupMember!!.contains(it.userId)) {
                    groupChat!!.groupMember!!.add(it.userId)
                }
            },
            {
                Toast.makeText(this, "Long Clicked ${it.userEmail}", Toast.LENGTH_SHORT).show()
            }
        )
        groupMemberDialog.groupAllUserListRecycler.adapter = userViewAdapter

        groupMemberDialog.groupAddMemberBtn.setOnClickListener {
            Log.d("GroupChatActivity", groupChat!!.groupMember.toString())


            val groupData = HashMap<String, Any?>()
                groupData["groupId"] = groupChat!!.groupId
                groupData["groupName"] = groupChat!!.groupName
                groupData["groupAdminId"] = groupChat!!.groupAdminId
                groupData["groupMember"] = groupChat!!.groupMember

            dbReference.collection("Groups").document(groupChat!!.groupId).set(groupData)
                .addOnSuccessListener {
                    Log.d("GroupChatActivity", "Group Members Added successfully written!")
                    Log.d("GroupChatActivity", groupChat!!.groupMember.toString())
                    Toast.makeText(this@GroupChatActivity, "Members Added Successfully", Toast.LENGTH_SHORT).show()
                    dialogBuilder.dismiss()
                }
                .addOnFailureListener { e ->
                    Log.w("GroupChatActivity", "Error writing document", e)
                }
        }


        retrieveUserList()
    }

    private fun retrieveUserList() {
        currUser = AppPref(this@GroupChatActivity).getUser()!!
        userList.clear()
        dbReference.collection("Users").get().addOnSuccessListener {
            for (dc in it) {
                if (dc.id != currUser.userId) {
                    userList.add(dc.toObject(User::class.java))
                    Log.d("UserListFragment", userList.toString())
                }
            }
            userViewAdapter.notifyDataSetChanged()
        }
//        dbReference.collection("Users")
//            .addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, e ->
//                if (e != null) {
//                    Log.w(ContentValues.TAG, "listen:error", e)
//                    return@EventListener
//                }
//
//                for (dc in querySnapshot!!.documentChanges) {
//                    when (dc.type) {
//                        DocumentChange.Type.ADDED -> {
//                            if (dc.document.id != currUser.userId) {
//                                userList.add(dc.document.toObject(User::class.java))
//                                println(userList)
//                                Log.d("UserListFragment", userList.toString())
//
//                                userViewAdapter.notifyDataSetChanged()
//                            }
//
//                        }
//                        DocumentChange.Type.MODIFIED -> {
//
//                        }
//                        DocumentChange.Type.REMOVED -> {
//
//                        }
//                    }
//                }
//            })
    }
}
