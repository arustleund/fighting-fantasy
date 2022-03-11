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
class DisplayTextClosure(private val getText: GameState.() -> String) : Closure {

    @Suppress("unused")
    constructor(element: Element) : this(element.getAttribute("id").toInt())

    constructor(textId: Int) : this({
        pageState.texts[textId] ?: throw IllegalArgumentException("Unknown text $textId")
    })

    constructor(text: String) : this({ text })

    override fun execute(gameState: GameState): Boolean {
        val text = gameState.getText()
        gameState.pageState.addToPagetext(text)
        return true
    }
}