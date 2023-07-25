package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.BattleState.BattleMessagePosition
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.asElementSequence
import rustleund.fightingfantasy.framework.base.optionalAttribute
import rustleund.fightingfantasy.framework.base.writeTag
import rustleund.fightingfantasy.framework.closures.Closure

class AddBattleMessageClosure(element: Element) : Closure {

    private val message = element.optionalAttribute("message") ?: ""
    private val messagePosition = element.optionalAttribute("messagePosition")
        ?.let { BattleMessagePosition.valueOf(it) } ?: BattleMessagePosition.END
    private val messageBody = writeTag(element.childNodes.asElementSequence().firstOrNull())

    override fun execute(gameState: GameState): Boolean {
        if (messagePosition == BattleMessagePosition.IMMEDIATE_END) {
            gameState.battleState?.immediatelyAppendBattleMessage(message + messageBody)
        } else {
            gameState.battleState?.addAdditionalMessage(messagePosition, message + messageBody)
        }
        return true
    }
}