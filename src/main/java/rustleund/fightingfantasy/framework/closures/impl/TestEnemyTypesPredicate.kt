package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.EnemyState
import rustleund.fightingfantasy.framework.base.optionalAttribute
import java.util.function.Predicate

class TestEnemyTypesPredicate(element: Element) : Predicate<GameState> {

    private val equalAny = element.optionalAttribute("equalAny")?.split(',').orEmpty()
    private val alwaysCheckAny = element.getAttribute("enemyId") == "any"
    private val enemyId = element.getAttribute("enemyId").takeIf { it != "any" }?.toInt() ?: 0

    override fun test(input: GameState): Boolean {
        val enemies = input.battleState.enemies
        val enemyIsOfAnyType: (EnemyState) -> Boolean = { e -> equalAny.any { e.isOfType(it) } }
        return if (alwaysCheckAny) enemies.enemies.any(enemyIsOfAnyType)
        else enemyIsOfAnyType(enemies.enemies[enemyId])
    }
}