package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.BattleState.BattleMessagePosition
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.optionalAttribute
import rustleund.fightingfantasy.framework.closures.Closure

class AddBattleMessageClosure(element: Element) : Closure {

    private val message = element.getAttribute("message")
    private val messagePosition = element.optionalAttribute("messagePosition")
        ?.let { BattleMessagePosition.valueOf(it) } ?: BattleMessagePosition.END

    override fun execute(gameState: GameState): Boolean {
        gameState.battleState.addAdditionalMessage(messagePosition, message)
        return true
    }
}