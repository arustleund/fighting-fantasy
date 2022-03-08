package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import java.util.function.Predicate

private fun Element.enemyId() = this.getAttribute("enemyId").toInt()

class TestEnemyStatPredicate(element: Element) : Predicate<GameState> by
TestStatPredicate(
    element,
    { it.battleState.enemies.enemies[element.enemyId()] },
    { it.battleState.currentAttackStrengths.getEnemyAttackStrength(element.enemyId()) }
)
