package rustleund.fightingfantasy.gamesave

import rustleund.fightingfantasy.framework.base.GameController
import javax.swing.AbstractAction
import java.awt.event.ActionEvent

class BackAction(private val gameController: GameController) : AbstractAction("Back") {

    override fun actionPerformed(e: ActionEvent) {
        val gameState = gameController.gameState
        gameState.popGameProgress()
        val latestGameProgress = gameState.popGameProgress()
        if (latestGameProgress != null) {
            gameState.playerState = latestGameProgress.playerState
            gameController.goToPage(latestGameProgress.pageId)
        }
    }
}