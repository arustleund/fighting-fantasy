package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.ClosureLoader

class AdjustEnemyScaleClosure(
    element: Element,
    closureLoader: ClosureLoader,
    battleEffectsLoader: BattleEffectsLoader
) : AdjustScaleClosure(element, closureLoader, battleEffectsLoader) {

    private val enemyId = element.optionalIntAttribute("enemyId")

    override fun entity(gameState: GameState): AbstractEntityState? {
        val enemies = gameState.battleState.enemies
        return enemyId?.let { enemies.enemies.getOrNull(it) } ?: enemies.firstNonDeadEnemy
    }
}