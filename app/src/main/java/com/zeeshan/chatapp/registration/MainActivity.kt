package com.zeeshan.chatapp.registration

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.splashScreen.SplashScreenActivity
import com.zeeshan.chatapp.utilities.EXTRA_MESSAGE

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startFragment()
    }

    private fun startFragment() {
        val fragmentCheck = intent.getStringExtra(EXTRA_MESSAGE)
        if (fragmentCheck == "LoginFragment()"){
            supportFragmentManager.beginTransaction().add(R.id.container,LoginFragment()).commit()
        }
        else{
            supportFragmentManager.beginTransaction().add(R.id.container,LogoutFragment()).commit()
        }
    }

}
