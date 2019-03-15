package com.zeeshan.chatapp.dashboard

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sdsmdg.harjot.vectormaster.VectorMasterView
import com.sdsmdg.harjot.vectormaster.models.PathModel
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.registration.MainActivity
import com.zeeshan.chatapp.splashScreen.SplashScreenActivity
import com.zeeshan.chatapp.utilities.AppPref
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {


    internal lateinit var outline: PathModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        bottom_nav.inflateMenu(R.menu.bottom_navigation)
        bottom_nav.selectedItemId = R.id.navigation_all_user
        bottom_nav.setOnNavigationItemSelectedListener(this@DashboardActivity)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.dashboardContainer, AllUserListFragment())
            .commit()

    }



    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.navigation_chats -> {
                draw(6)
                lin_id.x = bottom_nav.mFirstCurveControlPoint1.x.toFloat()
                fab.visibility = View.VISIBLE
                fab1.visibility = View.GONE
                fab2.visibility = View.GONE

                drawAnimation(fab)

                fragmentTransaction(RecentChatListFragment())
            }
            R.id.navigation_all_user -> {
                draw(2)
                lin_id.x = bottom_nav.mFirstCurveControlPoint1.x.toFloat()
                fab.visibility = View.GONE
                fab1.visibility = View.VISIBLE
                fab2.visibility = View.GONE

                drawAnimation(fab1)

                fragmentTransaction(AllUserListFragment())

            }
            R.id.navigation_profile -> {
                draw()
                lin_id.x = bottom_nav.mFirstCurveControlPoint1.x.toFloat()
                fab.visibility = View.GONE
                fab1.visibility = View.GONE
                fab2.visibility = View.VISIBLE

                drawAnimation(fab2)

                fragmentTransaction(ProfileFragment())

            }
        }
        return true
    }

    private fun fragmentTransaction(fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.dashboardContainer, fragment)
            commit()
        }
    }

    private fun drawAnimation(fab: VectorMasterView?) {
        outline = fab!!.getPathModelByName("outline")
        outline.strokeColor = Color.WHITE
        outline.trimPathEnd = 0f

        val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener { valueAnimator ->
            outline.trimPathEnd = valueAnimator.animatedValue as Float
            fab.update()
        }
        valueAnimator.start()

    }

    private fun draw(i: Int) {
        bottom_nav.mFirstCurveStartPoint.set(
            bottom_nav.mNavigationBarWidth / i - bottom_nav.curveCircleRadius * 2 - bottom_nav.curveCircleRadius / 3,
            0
        )
        bottom_nav.mFirstCurveEndPoint.set(
            bottom_nav.mNavigationBarWidth / i,
            bottom_nav.curveCircleRadius + bottom_nav.curveCircleRadius / 4
        )
        bottom_nav.mSecondCurveStartPoint = bottom_nav.mFirstCurveEndPoint

        bottom_nav.mSecondCurveEndPoint.set(
            bottom_nav.mNavigationBarWidth / i + bottom_nav.curveCircleRadius * 2 + bottom_nav.curveCircleRadius / 3,
            0
        )

        bottom_nav.mFirstCurveControlPoint1.set(
            bottom_nav.mFirstCurveStartPoint.x + bottom_nav.curveCircleRadius + bottom_nav.curveCircleRadius / 4,
            bottom_nav.mFirstCurveStartPoint.y
        )

        bottom_nav.mFirstCurveControlPoint2.set(
            bottom_nav.mFirstCurveEndPoint.x - bottom_nav.curveCircleRadius * 2 + bottom_nav.curveCircleRadius,
            bottom_nav.mFirstCurveEndPoint.y
        )

        bottom_nav.mSecondCurveControlPoint1.set(
            bottom_nav.mSecondCurveStartPoint.x + bottom_nav.curveCircleRadius * 2 - bottom_nav.curveCircleRadius,
            bottom_nav.mSecondCurveStartPoint.y
        )

        bottom_nav.mSecondCurveControlPoint2.set(
            bottom_nav.mSecondCurveEndPoint.x - (bottom_nav.curveCircleRadius + bottom_nav.curveCircleRadius / 4),
            bottom_nav.mSecondCurveEndPoint.y
        )

    }

    private fun draw() {
        bottom_nav.mFirstCurveStartPoint.set(
            bottom_nav.mNavigationBarWidth *10/12 - bottom_nav.curveCircleRadius * 2 - bottom_nav.curveCircleRadius / 3,
            0
        )
        bottom_nav.mFirstCurveEndPoint.set(
            bottom_nav.mNavigationBarWidth*10/12,
            bottom_nav.curveCircleRadius + bottom_nav.curveCircleRadius / 4
        )
        bottom_nav.mSecondCurveStartPoint = bottom_nav.mFirstCurveEndPoint

        bottom_nav.mSecondCurveEndPoint.set(
            bottom_nav.mNavigationBarWidth*10/12 + bottom_nav.curveCircleRadius * 2 + bottom_nav.curveCircleRadius / 3,
            0
        )

        bottom_nav.mFirstCurveControlPoint1.set(
            bottom_nav.mFirstCurveStartPoint.x + bottom_nav.curveCircleRadius + bottom_nav.curveCircleRadius / 4,
            bottom_nav.mFirstCurveStartPoint.y
        )

        bottom_nav.mFirstCurveControlPoint2.set(
            bottom_nav.mFirstCurveEndPoint.x - bottom_nav.curveCircleRadius * 2 + bottom_nav.curveCircleRadius,
            bottom_nav.mFirstCurveEndPoint.y
        )

        bottom_nav.mSecondCurveControlPoint1.set(
            bottom_nav.mSecondCurveStartPoint.x + bottom_nav.curveCircleRadius * 2 - bottom_nav.curveCircleRadius,
            bottom_nav.mSecondCurveStartPoint.y
        )

        bottom_nav.mSecondCurveControlPoint2.set(
            bottom_nav.mSecondCurveEndPoint.x - (bottom_nav.curveCircleRadius + bottom_nav.curveCircleRadius / 4),
            bottom_nav.mSecondCurveEndPoint.y
        )

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.dashboard_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menu_sign_out -> {
                showPopup()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showPopup() {
        val dialogBuilder = AlertDialog.Builder(this@DashboardActivity)
        val create: AlertDialog = dialogBuilder.create()

        dialogBuilder.setCancelable(false)

        dialogBuilder.setTitle("Signing Out!")
        dialogBuilder.setMessage("Do you want to Sign out!")

        dialogBuilder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val curUser = FirebaseAuth.getInstance()
                FirebaseFirestore.getInstance().collection("Users").document(curUser.currentUser!!.uid).update("registrationToken",null)
                curUser.signOut()
                val user = User("", "", "", "", "", "")
                AppPref(this@DashboardActivity).setUser(user)
                val intent = Intent(this@DashboardActivity, SplashScreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        dialogBuilder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                create.dismiss()
            }
        })
        dialogBuilder.create()
        dialogBuilder.show()
    }

}
