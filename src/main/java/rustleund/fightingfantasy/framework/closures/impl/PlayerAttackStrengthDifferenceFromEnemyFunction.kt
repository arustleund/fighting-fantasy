package rustleund.fightingfantasy.framework.closures.impl

import rustleund.fightingfantasy.framework.base.GameState
import java.util.function.Function

class PlayerAttackStrengthDifferenceFromEnemyFunction : Function<GameState, Int> {

    override fun apply(t: GameState): Int {
        val battleState = t.battleState
        return if (battleState != null) {
            val currentAttackStrengths = battleState.currentAttackStrengths
            val enemyIndex = battleState.enemies.enemies.indexOf(battleState.enemies.firstNonDeadEnemy)
            val enemyAttackStrength = currentAttackStrengths.getEnemyAttackStrength(enemyIndex)
            currentAttackStrengths.playerAttackStrength.total - (enemyAttackStrength?.total ?: 0)
        } else 0
    }
}