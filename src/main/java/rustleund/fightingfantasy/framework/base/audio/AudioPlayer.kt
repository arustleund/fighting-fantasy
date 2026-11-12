package rustleund.fightingfantasy.framework.base.audio

import jaco.mp3.player.MP3Player
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.GameView
import java.util.prefs.Preferences


class AudioPlayer : GameView {

    private val preferences = Preferences.userNodeForPackage(AudioPlayer::class.java)

    private var player = newPlayer()

    var narrationEnabled: Boolean = preferences.getBoolean(NARRATION_ENABLED_KEY, true)
        set(value) {
            field = value
            preferences.putBoolean(NARRATION_ENABLED_KEY, value)
            if (!value) {
                player.stop()
            }
        }

    /** Stops whatever narration is currently playing without changing the narrationEnabled setting. */
    fun stopNarration() {
        player.stop()
    }

    override fun update(gameState: GameState) {
        if (!narrationEnabled) {
            gameState.audioFilesToPlay.clear()
            return
        }
        while (gameState.audioFilesToPlay.isNotEmpty()) {
            val audioFile = gameState.audioFilesToPlay.pop()
            if (audioFile.interrupt || !player.isPlaying) {
                player.stop()
                player = newPlayer(audioFile)
                player.play()
                while (!player.isPlaying) {
                    Thread.sleep(5)
                }
            } else {
                player.add(audioFile.path.toFile())
            }
        }
    }

    private fun newPlayer(file: AudioFile? = null) =
        (if (file == null) MP3Player() else MP3Player(file.path.toFile())).setRepeat(false).setVolume(75)

    private companion object {
        const val NARRATION_ENABLED_KEY = "narrationEnabled"
    }
}
