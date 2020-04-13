package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

class TakePoisonDamageClosure(e: Element) : Closure {

    private val amount: Int = e.getAttribute("amount").toInt()

    override fun execute(gameState: GameState): Boolean {
        gameState.playerState.takePoisonDamage(amount)
        return true
    }
}