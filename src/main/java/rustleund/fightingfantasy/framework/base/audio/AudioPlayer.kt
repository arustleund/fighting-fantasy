package rustleund.fightingfantasy.framework.base.audio

import jaco.mp3.player.MP3Player
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.GameView


class AudioPlayer : GameView {

    private var player = MP3Player().setRepeat(false)

    override fun update(gameState: GameState) {
        while (gameState.audioFilesToPlay.isNotEmpty()) {
            val audioFile = gameState.audioFilesToPlay.pop()
            if (audioFile.interrupt || !player.isPlaying) {
                player.stop()
                player = MP3Player(audioFile.path.toFile()).setRepeat(false)
                player.play()
                while (!player.isPlaying) {
                    Thread.sleep(5)
                }
            } else {
                player.add(audioFile.path.toFile())
            }
        }
    }
}