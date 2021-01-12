package heartbeat.speed.game.objects

import android.app.Activity
import android.os.Handler
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import heartbeat.speed.game.R
import heartbeat.speed.game.managers.Audio
import heartbeat.speed.game.utils.DeviceAdapter
import kotlinx.android.synthetic.main.activity_main.*


class Cockroach(private val context: Activity) {
    private val view = ImageView(context)

    private val minLeftMargin = DeviceAdapter.getPxfromDp(10f, context)
    private val maxLeftMargin = DeviceAdapter.getPxfromDp(320f, context)
    private val minStartTopMargin = DeviceAdapter.getPxfromDp(-50f, context)
    private val maxStartTopMargin = DeviceAdapter.getPxfromDp(20f, context)
    private val maxTopMargin = DeviceAdapter.getPxfromDp(750f, context)
    private val cockroachWidth = 220
    private val cockroachHeight = 240

    private var alive = true
    private var hitSound = Audio("hit.ogg", context)

    init {
        view.setImageResource(R.drawable.cockroach)

        view.setOnClickListener {
            hitSound.play()
            view.setImageResource(R.drawable.dead_cockroach)
            if (alive) context.rate.text = (context.rate.text.toString().toInt() + 1).toString()
            alive = false
            Handler().postDelayed({ remove() }, 1000)
        }

        setClickable(false)

        val params = FrameLayout.LayoutParams(cockroachWidth, cockroachHeight)
        params.setMargins(
            (minLeftMargin..maxLeftMargin).random(),
            (minStartTopMargin..maxStartTopMargin).random(),
            0,
            0
        )
        context.rootLayout.addView(view, 1, params)
    }

    fun remove() {
        context.rootLayout.removeView(view)
    }

    fun isAlive() = alive

    fun setClickable(clickable: Boolean) {
        view.isClickable = clickable
    }

    fun move(step: Int): Boolean {
        val param = view.layoutParams as ViewGroup.MarginLayoutParams
        if (param.topMargin > maxTopMargin) return false
        val moveToLeft = if (param.leftMargin - step >= minLeftMargin) -step else 0
        val moveToRight = if (param.leftMargin + step <= maxLeftMargin) step else 0
        param.setMargins(
            param.leftMargin + (moveToLeft..moveToRight).random(),
            param.topMargin + (0..step).random(),
            param.rightMargin,
            param.bottomMargin
        )
        view.layoutParams = param
        return true
    }
}