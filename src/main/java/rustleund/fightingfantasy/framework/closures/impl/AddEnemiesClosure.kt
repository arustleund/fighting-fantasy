/*
 * Created on Oct 8, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.closures.Closure

class AddEnemiesClosure(element: Element, closureLoader: ClosureLoader) : Closure {

    private val enemiesToAdd = getChildElementsByName(element, "enemy")
        .map { EnemyState(it, closureLoader) }.toList()
    private val battleId = element.getAncestorByName("battle")?.optionalIntAttribute("id")
        ?: throw IllegalArgumentException("No ancestor battle element or ID found")

    private var waitTime = element.getAttribute("wait").toInt()

    override fun execute(gameState: GameState): Boolean {
        if (waitTime == 0) {
            val battleState = gameState.pageState.getBattle(battleId)
            enemiesToAdd.forEach { battleState.enemies.addEnemy(it) }
        }
        waitTime--
        return true
    }
}