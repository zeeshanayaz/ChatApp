package com.zeeshan.chatapp.dashboard

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.adapter.UserChatListAdapter
import com.zeeshan.chatapp.model.ChatMessage
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.utilities.AppPref
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var chatAdapter : UserChatListAdapter
    var userChatList: ArrayList<ChatMessage> = ArrayList()
    lateinit var user: User
    private lateinit var dbReference: FirebaseFirestore
    lateinit var currUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        dbReference = FirebaseFirestore.getInstance()

        if (intent != null && intent.extras != null){
            val extras = intent.extras
            user = extras.getSerializable("user") as User

        }
        else{
            finish()
        }


        chatMessageListRecycler.layoutManager = LinearLayoutManager(this@ChatActivity)
        chatAdapter = UserChatListAdapter(this@ChatActivity, userChatList)
        chatMessageListRecycler.adapter = chatAdapter

        recieveMessages()

        sendMessageButton.setOnClickListener { message ->
            if (!messageTextField.text.trim().isEmpty()){
                currUser = AppPref(this@ChatActivity).getUser()!!

                var chatID = user.userId + "-" + currUser?.userId
                val list = chatID.split("-")
                val sorted = list.sorted()
                chatID = sorted[0] + "-" + sorted[1]

                val msgID = System.currentTimeMillis().toString()

                val chatMessage = ChatMessage(
                    chatID,
                    messageTextField.text.toString().trim(),
                    System.currentTimeMillis(),
                    msgID,
                    currUser?.userId!!
                )

                dbReference.collection("Chats").document("User-Chat").collection(chatID).document(msgID).set(chatMessage)
//                (chatID +"\""+ msgID).set(chatMessage)
                    .addOnSuccessListener {
                        Log.d("ChatActivity", "DocumentSnapshot/Message successfully Send!")
                    }
                    .addOnFailureListener {
                        Log.w("ChatActivity", "Error sending Message", it)
                    }


                messageTextField.setText("")



            }
        }
    }

    private fun recieveMessages() {
        currUser = AppPref(this@ChatActivity).getUser()!!

        var chatID = user.userId + "-" + currUser?.userId
        val list = chatID.split("-")
        val sorted = list.sorted()
        chatID = sorted[0] + "-" + sorted[1]


        dbReference.collection("Chats").document("User-Chat").collection(chatID)
            .addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in querySnapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            if (dc.document.id != currUser.userId) {
                                userChatList.add(dc.document.toObject(ChatMessage::class.java))
                                println(userChatList)
                                Log.d("UserListFragment", userChatList.toString())
                            }
//                            Log.d(TAG, "New city: ${dc.document.data}")

                        }
                        DocumentChange.Type.MODIFIED -> {

                        }
                        DocumentChange.Type.REMOVED -> {

                        }
                    }
                }
                chatMessageListRecycler.scrollToPosition(userChatList.size - 1)
                chatAdapter.notifyDataSetChanged()
            })
    }
}
