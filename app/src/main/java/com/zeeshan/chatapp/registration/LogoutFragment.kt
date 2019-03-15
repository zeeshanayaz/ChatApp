package com.zeeshan.chatapp.registration


import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.firestore.FirebaseFirestore

import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.dashboard.DashboardActivity
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.utilities.AppPref
import kotlinx.android.synthetic.main.fragment_logout.*

/**
 * A simple [Fragment] subclass.
 *
 */
class LogoutFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: FirebaseFirestore
    private lateinit var progress: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logout, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Snackbar.make(view, "All field are required.", Snackbar.LENGTH_SHORT).setAction("Action", null).show()

        auth = FirebaseAuth.getInstance()
        dbReference = FirebaseFirestore.getInstance()
        progress = ProgressDialog(activity!!)




        createUserBtn.setOnClickListener {


            if (!createTextUserName.text.isNullOrEmpty() && !createTextEmailAddress.text.isNullOrEmpty() && !createTextPassword.text.isNullOrEmpty()) {
                Snackbar.make(view, "Connecting to Server", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
                progress.setMessage("Registering user...")
                progress.setCancelable(false)
                progress.show()
                registerUser(
                    createTextUserName.text.trim().toString(),
                    createTextEmailAddress.text.trim().toString(),
                    createTextPassword.text.toString()
                )
            } else {
                createTextUserName.setError("Error")
                createTextEmailAddress.setError("Error")
                createTextPassword.setError("Error")
                Snackbar.make(view, "All field are required.", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            }
        }

        createLoginBtn.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, LoginFragment())
                .commit()
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val authResult = it.result
                auth.currentUser!!.getIdToken(true).addOnSuccessListener(object : OnSuccessListener<GetTokenResult> {
                    override fun onSuccess(getTokenResult: GetTokenResult?) {
                        val tokenId = getTokenResult!!.token

                        val user = User("${authResult?.user?.uid}", name, email, null, null, tokenId)
                        saveUserDataToFirestore(user)
                        AppPref(activity!!).setUser(user)

                        progress.dismiss()
                        navigateToDashboard()
                    }


                })


            } else {
                progress.dismiss()
                Toast.makeText(activity, "User not created ${it.exception.toString()}", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun saveUserDataToFirestore(user: User) {
        dbReference.collection("Users").document(user.userId).set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error writing document", it)
            }
    }

    private fun navigateToDashboard() {
        val intent = Intent(activity!!, DashboardActivity::class.java).apply {
            //                putExtra(EXTRA_MESSAGE, "LoginFragment()")
        }
        startActivity(intent)
    }

}
