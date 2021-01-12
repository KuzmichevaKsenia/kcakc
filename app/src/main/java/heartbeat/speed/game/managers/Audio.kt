package heartbeat.speed.game.managers

import android.app.Activity
import android.media.AudioAttributes
import android.media.SoundPool
import android.widget.Toast
import java.io.IOException

open class Audio(private val fileName: String, private val context: Activity) {
    protected val soundPool: SoundPool = SoundPool.Builder().setAudioAttributes(
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    ).build()

    protected var soundId = loadSound()

    fun play() {
        if (checkSoundId()) safePlay()
    }

    protected open fun safePlay() {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    private fun loadSound(): Int {
        val afd = try {
            context.assets.openFd(fileName)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                context, "Не могу загрузить файл $fileName",
                Toast.LENGTH_SHORT
            ).show()
            return -1
        }
        return soundPool.load(afd, 1)
    }

    private fun checkSoundId() = soundId >= 0
}