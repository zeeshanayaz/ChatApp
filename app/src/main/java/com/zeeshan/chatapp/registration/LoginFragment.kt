package com.zeeshan.chatapp.registration


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.dashboard.DashboardActivity
import com.zeeshan.chatapp.utilities.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 *
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Snackbar.make(view,"All field are required.", Snackbar.LENGTH_SHORT).setAction("Action",null).show()

        loginLoginButton.setOnClickListener {
            navigateToDashboard()
        }

        loginCreateUserBtn.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, LogoutFragment())
                .commit()
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(activity!!, DashboardActivity::class.java).apply {
            //                putExtra(EXTRA_MESSAGE, "LoginFragment()")
        }
        startActivity(intent)


    }


}
