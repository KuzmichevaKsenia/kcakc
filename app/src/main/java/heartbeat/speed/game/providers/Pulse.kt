package heartbeat.speed.game.providers

import android.annotation.SuppressLint
import android.view.SurfaceView
import android.widget.ImageView
import android.widget.TextView
import heartbeat.speed.game.R
import net.kibotu.heartrateometer.HeartRateOmeter
import net.kibotu.kalmanrx.jama.Matrix
import net.kibotu.kalmanrx.jkalman.JKalman

class Pulse(
    private val surfaceView: SurfaceView,
    private val fingerStateView: TextView,
    private val bpmView: TextView,
    private val beatView: ImageView,
    siView: TextView,
    siDescriptionView: TextView
) {

    private val hrv = HRV(siView, siDescriptionView, surfaceView)
    private var lastTimeBeat = 0L

    @SuppressLint("CheckResult")
    fun start() {
        val kalman = JKalman(2, 1)

        // measurement [x]
        val m = Matrix(1, 1)

        // transitions for x, dx
        val tr = arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0))
        kalman.transition_matrix = Matrix(tr)

        // 1s somewhere?
        kalman.error_cov_post = kalman.error_cov_post.identity()

        HeartRateOmeter()
            .withAverageAfterSeconds(3)
            .setFingerDetectionListener { fingerDetected ->
                if (fingerDetected) {
                    fingerStateView.text = ""
                } else {
                    lastTimeBeat = 0
                    fingerStateView.text = "Приложите палец к камере, чтобы продолжить"
                    bpmView.text = "0"
                    beatView.setImageResource(-1)
                }
            }
            .bpmUpdates(surfaceView)
            .subscribe({

                if (it.value == 0) {
                    bpmView.text = "0"
                    return@subscribe
                }

                m.set(0, 0, it.value.toDouble())

                // state [x, dx]
                val s = kalman.Predict()

                // corrected state [x, dx]
                val c = kalman.Correct(m)

                bpmView.text = c.get(0, 0).toInt().toString()
                beatView.setImageResource(if (it.type == HeartRateOmeter.PulseType.OFF) -1 else {
                    val timeBeat = System.currentTimeMillis()
                    if(lastTimeBeat != 0L) {
                        val diff = timeBeat - lastTimeBeat
                        if (diff in 400..1600 ) hrv.add(timeBeat - lastTimeBeat)
                    }
                    lastTimeBeat = timeBeat
                    R.drawable.lamp
                })

            }, Throwable::printStackTrace)
    }

    fun getSiRate() = hrv.getSiRate()
}