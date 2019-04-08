package com.zeeshan.chatapp.dashboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.utilities.EXTRA_MESSAGE

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        startFragment()

    }

    private fun startFragment() {
        val fragmentCheck = intent.getStringExtra(EXTRA_MESSAGE)
        if (fragmentCheck == "ProfileFragment()"){
            supportFragmentManager.beginTransaction().add(R.id.menuActivityContainer,ProfileFragment()).commit()
        }
    }
}
