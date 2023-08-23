package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import java.util.function.Predicate

private fun Element.enemyIdx() = this.getAttribute("enemyIdx").toInt()

class TestEnemyStatPredicate(element: Element) : Predicate<GameState> by
TestStatPredicate(
    element,
    { it.battleState?.enemies?.enemies?.get(element.enemyIdx()) ?: throw IllegalStateException("No battle in progress") },
    { it.battleState?.currentAttackStrengths?.getEnemyAttackStrength(element.enemyIdx()) }
)
