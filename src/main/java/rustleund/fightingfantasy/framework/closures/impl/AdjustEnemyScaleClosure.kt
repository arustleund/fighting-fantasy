package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.Closure

class AdjustEnemyScaleClosure(
    element: Element
) : Closure by AdjustScaleClosure(
    element,
    { gameState ->
        val enemies = gameState.battleState.enemies
        element.optionalIntAttribute("enemyId")?.let { enemies.enemies.getOrNull(it) } ?: enemies.firstNonDeadEnemy
    }
)