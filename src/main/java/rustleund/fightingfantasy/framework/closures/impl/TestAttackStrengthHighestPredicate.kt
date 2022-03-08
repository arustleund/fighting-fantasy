package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.booleanAttribute
import rustleund.fightingfantasy.framework.base.intAttribute
import java.util.function.Predicate

class TestAttackStrengthHighestPredicate(e: Element) : Predicate<GameState> {

    private val player = e.booleanAttribute("player", false)
    private val enemyId = e.intAttribute("enemyId", 0)

    override fun test(gameState: GameState): Boolean {
        val currentAttackStrengths = gameState.battleState.currentAttackStrengths
        return if (player) !currentAttackStrengths.playerHit
        else currentAttackStrengths.enemyHasHighestAttackStrength(enemyId)
    }
}