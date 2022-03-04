package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element

private fun Element.enemyId() = this.getAttribute("enemyId").toInt()

class TestEnemyStatPredicate(element: Element) :
    TestStatPredicate(
        element,
        { it.battleState.enemies.enemies[element.enemyId()] },
        { it.battleState.currentAttackStrengths.getEnemyAttackStrength(element.enemyId()).total }
    )
