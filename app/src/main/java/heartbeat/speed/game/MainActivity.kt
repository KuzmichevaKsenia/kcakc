package heartbeat.speed.game

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import heartbeat.speed.game.managers.Audio
import heartbeat.speed.game.managers.CyclicAudio
import heartbeat.speed.game.objects.Cockroaches
import heartbeat.speed.game.providers.Pulse
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 123
        private const val APP_PREFERENCES = "kcakc_settings"
        private const val APP_PREFERENCES_MAX_RATE = "max_rate"
    }

    private val cockroaches = Cockroaches()
    private lateinit var pref: SharedPreferences
    private lateinit var pulse: Pulse
    private lateinit var crawlSound: CyclicAudio
    private lateinit var failSound: Audio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        crawlSound = CyclicAudio("crawl.ogg", this)
        failSound = Audio("fail.ogg", this)
        pulse = Pulse(preview, finger, bpm, beat, si, siDescription)

        playButton.setOnClickListener { startNewGame() }
    }

    private fun startWithPermissionCheck() {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            checkPermissions(REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA)
            return
        }
        pulse.start()
    }

    private fun startNewGame() {
        playButton.visibility = View.INVISIBLE
        rateBars.visibility = View.VISIBLE
        val mainHandler = Handler(Looper.getMainLooper())
        var prevState = false
        mainHandler.post(object : Runnable {
            override fun run() {
                val gameIsOn = bpm.text.toString() != "0" && si.text.toString() != "0"
                if (prevState != gameIsOn) setGameState(gameIsOn)
                cockroaches.setClickable(gameIsOn)
                if (gameIsOn) {
                    if (!cockroaches.step(
                            pulse.getSiRate(),
                            this@MainActivity
                        )
                    ) return gameOver()
                }
                prevState = gameIsOn
                mainHandler.postDelayed(this, 10)
            }
        })
    }

    private fun gameOver() {
        failSound.play()

        fun finish() {
            playButton.visibility = View.VISIBLE
        }

        val curRate = rate.text.toString().toInt()
        val curMaxRate = maxRate.text.toString().toInt()
        var improvingText = ""
        if (curMaxRate < curRate) {
            maxRate.text = curRate.toString()
            improvingText = " Вы побили свой рекорд!!!"
        }
        setGameState(false)
        AlertDialog.Builder(this).apply {
            setTitle("Игра окончена")
            setMessage("Убито тараканов: $curRate.$improvingText Играть снова?")
            setNegativeButton("Нет") { _, _ -> finish() }
            setPositiveButton("Да") { _, _ ->
                startNewGame()
            }
            setOnCancelListener { finish() }
            show()
        }

        rateBars.visibility = View.INVISIBLE
        rate.text = "0"
        cockroaches.clean()
    }

    private fun setGameState(state: Boolean) {
        if (state) crawlSound.play()
        else crawlSound.pause()
    }

    override fun onResume() {
        super.onResume()
        if (pref.contains(APP_PREFERENCES_MAX_RATE)) {
            maxRate.text = pref.getInt(APP_PREFERENCES_MAX_RATE, 0).toString()
        }
        startWithPermissionCheck()
    }

    override fun onPause() {
        super.onPause()
        val editor = pref.edit()
        editor.putInt(APP_PREFERENCES_MAX_RATE, maxRate.text.toString().toInt())
        editor.apply()
    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        when {
            !hasPermission(*permissionsId) -> try {
                ActivityCompat.requestPermissions(this, permissionsId, callbackId)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun hasPermission(vararg permissionsId: String): Boolean {
        var hasPermission = true

        permissionsId.forEach { permission ->
            hasPermission = hasPermission
                    && ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        return hasPermission
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startWithPermissionCheck()
                }
            }
        }
    }

}
