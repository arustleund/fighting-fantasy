package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.intAttribute
import rustleund.fightingfantasy.framework.closures.Closure

class SaveEnemyStaminaClosure(
    e: Element
) : Closure {

    private val id = e.intAttribute("id")

    override fun execute(gameState: GameState): Boolean {
        val enemy = gameState.battleState?.enemies?.enemies?.firstOrNull { it.id == id }
        return if (enemy == null) false else {
            gameState.playerState.savedEnemyStamina[id] = enemy.stamina.deepCopy()
            true
        }
    }
}