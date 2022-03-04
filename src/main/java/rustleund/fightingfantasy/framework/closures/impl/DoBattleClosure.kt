package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

class DoBattleClosure(private val id: Int) : Closure {

    @Suppress("unused")
    constructor(element: Element) : this(element.getAttribute("id").toInt())

    override fun execute(gameState: GameState): Boolean {
        val battleState = gameState.pageState.getBattle(id)
        gameState.isBattleInProgress = true
        gameState.battleState = battleState
        if (battleState.battleIsNotOver()) {
            battleState.incrementGameState()
            battleState.doAfterPossibleStaminaChange()
            return true
        }
        return false
    }
}