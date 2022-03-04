/*
 * Created on Jul 5, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

/**
 * @author rustlea
 */
class DisplayTextClosure(private val textId: Int) : Closure {

    @Suppress("unused")
    constructor(element: Element) : this(element.getAttribute("id").toInt())

    override fun execute(gameState: GameState): Boolean {
        val text = gameState.pageState.texts[textId]
        gameState.pageState.addToPagetext(text)
        return true
    }
}