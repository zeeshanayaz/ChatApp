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
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.dashboard.DashboardActivity
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.utilities.AppPref
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 *
 */
class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: FirebaseFirestore
    private lateinit var progress: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Snackbar.make(view, "All field are required.", Snackbar.LENGTH_SHORT).setAction("Action", null).show()

        auth = FirebaseAuth.getInstance()
        dbReference = FirebaseFirestore.getInstance()
        progress = ProgressDialog(activity!!)

        loginLoginButton.setOnClickListener {

            if (!loginTextEmailAddress.text.trim().isNullOrEmpty() && !loginTextPassword.text.trim().isNullOrEmpty()) {
                Snackbar.make(view, "Connecting to Server", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
                progress.setMessage("Registering user...")
                progress.setCancelable(false)
                progress.show()
//                progressBar.visibility = View.VISIBLE
                auhenticateUser(loginTextEmailAddress.text.trim().toString(), loginTextPassword.text.trim().toString())
            } else {
                loginTextEmailAddress.setError("Error")
                loginTextPassword.setError("Error")
                Snackbar.make(view, "All field are required.", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            }
        }

        loginCreateUserBtn.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, LogoutFragment())
                .commit()
        }
    }

    private fun auhenticateUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                auth.currentUser!!.getIdToken(true).addOnSuccessListener(object : OnSuccessListener<GetTokenResult>{
                    override fun onSuccess(getTokenResult: GetTokenResult?) {
                        val tokenId = getTokenResult!!.token
                        dbReference.collection("Users").document(it.user.uid).update("registrationToken","$tokenId")

                        getUserDataFromFirestore(it.user.uid, dbReference)
                    }

                })


            }
            .addOnFailureListener {
                progress.dismiss()
//                progressBar.visibility = View.GONE
                Toast.makeText(activity, "Error in signin ${it.toString()}", Toast.LENGTH_LONG).show()
            }

    }

    private fun getUserDataFromFirestore(uid: String, dbReference: FirebaseFirestore) {
        dbReference.collection("Users").document("$uid").get().addOnSuccessListener {
            if (it.exists()) {
                val user = it.toObject(User::class.java)
                AppPref(activity!!).setUser(user!!)
                Log.d(TAG, "AppPref successfully written!")
                progress.dismiss()
//                progressBar.visibility = View.GONE
                navigateToDashboard()
            }
        }
    }


    private fun navigateToDashboard() {
        val intent = Intent(activity!!, DashboardActivity::class.java).apply {
            //                putExtra(EXTRA_MESSAGE, "LoginFragment()")
        }
        startActivity(intent)


    }


}
