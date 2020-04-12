package rustleund.fightingfantasy.framework.closures.impl

import com.google.common.base.Predicate
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState

class TestAttackStrengthHighestPredicate(e: Element) : Predicate<GameState> {

    private val player = e.getAttribute("player")?.toBoolean() ?: false
    private val enemyId = e.getAttribute("enemyId")?.toInt() ?: 0

    override fun apply(p0: GameState?): Boolean {
        val gameState = p0 ?: return false // shouldn't be null
        val currentAttackStrengths = gameState.battleState.currentAttackStrengths
        return if (player) currentAttackStrengths.playerHasHighestAttackStrength()
        else currentAttackStrengths.enemyHasHighestAttackStrength(enemyId)
    }
}