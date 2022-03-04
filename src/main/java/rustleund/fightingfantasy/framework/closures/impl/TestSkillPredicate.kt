package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.util.DiceRoller.rollDice
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.AbstractEntityState
import rustleund.fightingfantasy.framework.base.intAttribute
import java.util.function.Function
import java.util.function.Predicate

class TestSkillPredicate(
    element: Element,
    private val gameStateToEntity: Function<in GameState, out AbstractEntityState>
) : Predicate<GameState> {

    private val diceRollAdjustment = element.intAttribute("diceRollAdjustment", 0)

    override fun test(gameState: GameState): Boolean {
        val entity = gameStateToEntity.apply(gameState)
        return rollDice(2) + diceRollAdjustment <= entity.skill.currentValue
    }
}