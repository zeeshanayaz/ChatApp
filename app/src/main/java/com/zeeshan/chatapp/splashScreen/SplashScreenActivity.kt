package com.zeeshan.chatapp.splashScreen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.dashboard.DashboardActivity
import com.zeeshan.chatapp.registration.MainActivity
import com.zeeshan.chatapp.utilities.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var auth : FirebaseAuth = FirebaseAuth.getInstance()


        if (auth.currentUser != null) {
            startActivity(Intent(this@SplashScreenActivity, DashboardActivity::class.java))
        }

        splashLogInBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, "LoginFragment()")
            }
            startActivity(intent)
        }
        splashLogOutBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, "LogoutFragment()")
            }
            startActivity(intent)
        }
    }
}
