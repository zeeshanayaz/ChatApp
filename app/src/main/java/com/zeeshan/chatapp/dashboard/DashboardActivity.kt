package com.zeeshan.chatapp.dashboard

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.sdsmdg.harjot.vectormaster.VectorMasterView
import com.sdsmdg.harjot.vectormaster.models.PathModel
import com.zeeshan.chatapp.R
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {


    internal lateinit var outline: PathModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        bottom_nav.inflateMenu(R.menu.bottom_navigation)
        bottom_nav.selectedItemId = R.id.navigation_chats
        bottom_nav.setOnNavigationItemSelectedListener(this@DashboardActivity)

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

                Toast.makeText(this, "Clicked on Recent Chat", Toast.LENGTH_SHORT).show()

            }
            R.id.navigation_all_user -> {
                draw(2)
                lin_id.x = bottom_nav.mFirstCurveControlPoint1.x.toFloat()
                fab.visibility = View.GONE
                fab1.visibility = View.VISIBLE
                fab2.visibility = View.GONE

                drawAnimation(fab1)

                Toast.makeText(this, "Clicked on user List", Toast.LENGTH_SHORT).show()
            }
            R.id.navigation_profile -> {
                draw()
                lin_id.x = bottom_nav.mFirstCurveControlPoint1.x.toFloat()
                fab.visibility = View.GONE
                fab1.visibility = View.GONE
                fab2.visibility = View.VISIBLE

                drawAnimation(fab2)

                Toast.makeText(this, "Clicked on Profile", Toast.LENGTH_SHORT).show()
            }
        }
        return true
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
}
