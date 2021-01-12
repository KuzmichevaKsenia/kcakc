package heartbeat.speed.game.objects

import android.app.Activity
import heartbeat.speed.game.providers.HRV
import heartbeat.speed.game.utils.DeviceAdapter

class Cockroaches {
    private val list = mutableListOf<Cockroach>()
    private var movesInPeriod = 0

    fun clean() {
        list.removeAll {
            it.remove()
            true
        }
    }

    fun setClickable(clickable: Boolean) {
        for (cockroach in list) cockroach.setClickable(clickable)
    }

    fun step(siRate: HRV.SiRate, context: Activity): Boolean {
        removeCorpses()
        if (getAddPeriod(siRate) in 0 until movesInPeriod) {
            list.add(Cockroach(context))
            movesInPeriod = 0
        }
        movesInPeriod++
        return move(getMovePxStep(siRate, context))
    }

    private fun removeCorpses() {
        list.removeIf { !it.isAlive() }
    }

    private fun move(step: Int): Boolean {
        for (cockroach in list) {
            if (!cockroach.move(step)) {
                return false
            }
        }
        return true
    }

    private fun getMovePxStep(siRate: HRV.SiRate, context: Activity): Int {
        return when (siRate) {
            HRV.SiRate.UNKNOWN -> DeviceAdapter.getPxfromDp(0f, context)
            HRV.SiRate.LOW -> DeviceAdapter.getPxfromDp(5f, context)
            HRV.SiRate.LOWER -> DeviceAdapter.getPxfromDp(4.5f, context)
            HRV.SiRate.NORMAL -> DeviceAdapter.getPxfromDp(4f, context)
            HRV.SiRate.PRE_HIGHER -> DeviceAdapter.getPxfromDp(3.5f, context)
            HRV.SiRate.HIGHER -> DeviceAdapter.getPxfromDp(3f, context)
            HRV.SiRate.HIGH -> DeviceAdapter.getPxfromDp(2.5f, context)
            HRV.SiRate.OVER_HIGH -> DeviceAdapter.getPxfromDp(1.5f, context)
        }
    }

    private fun getAddPeriod(siRate: HRV.SiRate): Int {
        return when (siRate) {
            HRV.SiRate.UNKNOWN -> -1
            HRV.SiRate.LOW -> 30
            HRV.SiRate.LOWER -> 35
            HRV.SiRate.NORMAL -> 40
            HRV.SiRate.PRE_HIGHER -> 45
            HRV.SiRate.HIGHER -> 50
            HRV.SiRate.HIGH -> 55
            HRV.SiRate.OVER_HIGH -> 60
        }
    }
}