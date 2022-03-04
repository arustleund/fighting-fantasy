package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.BattleState.BattleMessagePosition
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.optionalAttribute
import rustleund.fightingfantasy.framework.closures.Closure

class ClearBattleMessageClosure(e: Element) : Closure {

    private val positionsToClear = e.optionalAttribute("positionsToClear")
        ?.splitToSequence(',')
        ?.map { pos -> BattleMessagePosition.valueOf(pos) }
        ?.toList()

    override fun execute(gameState: GameState): Boolean {
        if (positionsToClear == null) {
            gameState.battleState.clearAllAdditionalMessages()
        } else {
            positionsToClear.forEach { gameState.battleState.clearAdditionalMessage(it) }
        }
        return true
    }
}