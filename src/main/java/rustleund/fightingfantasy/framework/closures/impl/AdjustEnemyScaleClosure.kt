package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader

class AdjustEnemyScaleClosure(
    element: Element,
    closureLoader: ClosureLoader,
    battleEffectsLoader: BattleEffectsLoader
) : Closure by AdjustScaleClosure(
    element,
    closureLoader,
    battleEffectsLoader,
    { gameState ->
        val enemies = gameState.battleState.enemies
        element.optionalIntAttribute("enemyId")?.let { enemies.enemies.getOrNull(it) } ?: enemies.firstNonDeadEnemy
    }
)