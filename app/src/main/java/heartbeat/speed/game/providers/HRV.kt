package heartbeat.speed.game.providers

import android.graphics.Color
import android.view.SurfaceView
import android.widget.TextView

class HRV(
    private val siView: TextView,
    private val siDescriptionView: TextView,
    private val surfaceView: SurfaceView
) {

    enum class SiRate { UNKNOWN, LOW, LOWER, NORMAL, PRE_HIGHER, HIGHER, HIGH, OVER_HIGH }

    private val msRRintervals = mutableListOf<Long>()

    fun add(interval: Long) {
        msRRintervals.add(interval)
        updateSi()
        val siRate = getSiRate()
        updateSiDescription(siRate)
        updateBackground(siRate)
    }

    fun getSiRate() = when (siView.text.toString().toInt()) {
        0 -> SiRate.UNKNOWN
        in 1..10 -> SiRate.LOW
        in 10..20 -> SiRate.LOWER
        in 20..120 -> SiRate.NORMAL
        in 120..200 -> SiRate.PRE_HIGHER
        in 200..500 -> SiRate.HIGHER
        in 500..800 -> SiRate.HIGH
        else -> SiRate.OVER_HIGH
    }

    private fun updateSi() {
        val msMo = msRRintervals.average()
        val dx = 200
        val amo =
            msRRintervals.filter { it >= msMo - dx && it <= msMo + dx }.size.toDouble() * 100 / msRRintervals.size
        val mxdmn = (msRRintervals.max()!! - msRRintervals.min()!!).toDouble() / 1000
        val si = (amo / (2 * mxdmn * msMo / 1000)).toInt()
        if (si in 0..2000) siView.text = si.toString()
    }

    private fun updateSiDescription(siRate: SiRate) {
        siDescriptionView.text = when (siRate) {
            SiRate.UNKNOWN -> ""
            SiRate.LOW -> "Резервы организма исчерпаны"
            SiRate.LOWER -> "Вы на пределе"
            SiRate.NORMAL -> "Норма"
            SiRate.PRE_HIGHER -> "Верхняя граница нормы"
            SiRate.HIGHER -> "Выше нормы"
            SiRate.HIGH -> "Очень высокий"
            SiRate.OVER_HIGH -> "Ваше сердце работает на износ"
        }
    }

    private fun updateBackground(siRate: SiRate) {
        surfaceView.setBackgroundColor(
            when (siRate) {
                SiRate.UNKNOWN -> Color.parseColor("#ffffff")
                SiRate.LOW -> Color.parseColor("#ff4c5b")
                SiRate.LOWER -> Color.parseColor("#ffff00")
                SiRate.NORMAL -> Color.parseColor("#008000")
                SiRate.PRE_HIGHER -> Color.parseColor("#ffff00")
                SiRate.HIGHER -> Color.parseColor("#ffa500")
                SiRate.HIGH -> Color.parseColor("#ff4c5b")
                SiRate.OVER_HIGH -> Color.parseColor("#8b0000")
            }
        )
    }
}