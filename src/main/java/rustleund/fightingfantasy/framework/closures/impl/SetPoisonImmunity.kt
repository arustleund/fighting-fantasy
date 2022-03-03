package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.booleanAttribute
import rustleund.fightingfantasy.framework.closures.Closure

class SetPoisonImmunity(element: Element) : Closure {

    private val immune: Boolean = element.booleanAttribute("immune", true)

    override fun execute(gameState: GameState): Boolean {
        gameState.playerState.isPoisonImmunity = immune
        return true
    }
}