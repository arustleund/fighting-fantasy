/*
 * Created on Oct 9, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.optionalIntAttribute
import rustleund.fightingfantasy.framework.closures.Closure

/**
 * @author rustlea
 */
class RemoveItemClosure(element: Element) : Closure {

    private val itemId = element.getAttribute("id").toInt()
    private val amount = element.optionalIntAttribute("amount") ?: 1

    override fun execute(gameState: GameState): Boolean {
        repeat(amount) {
            gameState.playerState.removeOneOfItem(itemId)
        }
        return true
    }
}