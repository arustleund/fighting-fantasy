/*
 * Created on Oct 14, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.booleanAttribute
import rustleund.fightingfantasy.framework.closures.Closure

/**
 * @author rustlea
 */
class SetFlagClosure(element: Element) : Closure {

    private val flagId = element.getAttribute("id").toInt()
    private val flagValue = element.booleanAttribute("value", true)

    override fun execute(gameState: GameState): Boolean {
        gameState.playerState.setFlag(flagId, flagValue)
        return true
    }
}