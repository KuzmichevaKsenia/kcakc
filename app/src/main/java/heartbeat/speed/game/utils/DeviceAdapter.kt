package heartbeat.speed.game.utils

import android.app.Activity
import android.util.TypedValue

object DeviceAdapter {
    fun getPxfromDp(dp: Float, context: Activity) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}