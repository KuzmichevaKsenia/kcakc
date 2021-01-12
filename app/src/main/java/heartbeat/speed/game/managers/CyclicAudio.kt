package heartbeat.speed.game.managers

import android.app.Activity

class CyclicAudio(fileName: String, context: Activity) : Audio(fileName, context) {
    init {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                soundId = soundPool.play(soundId, 1f, 1f, 1, -1, 1f)
                soundPool.pause(soundId)
            }
        }
    }

    fun pause() {
        soundPool.pause(soundId)
    }

    override fun safePlay() {
        soundPool.resume(soundId)
    }
}