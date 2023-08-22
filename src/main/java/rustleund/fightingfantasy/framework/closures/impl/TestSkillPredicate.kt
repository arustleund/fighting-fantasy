package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.util.DiceRoller.rollDice
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.AbstractEntityState
import rustleund.fightingfantasy.framework.base.optionalAttribute
import java.util.function.Function
import java.util.function.Predicate

class TestSkillPredicate(
    element: Element,
    private val gameStateToEntity: Function<in GameState, out AbstractEntityState>
) : Predicate<GameState> {

    private val diceRollAdjustmentFormula = element.optionalAttribute("diceRollAdjustment")

    override fun test(gameState: GameState): Boolean {
        val entity = gameStateToEntity.apply(gameState)
        val diceRollResult = rollDice(2)
        val newFormula = diceRollAdjustmentFormula.portOldFormula()
        val finalResult = if (newFormula == null) diceRollResult else keval()
            .withPlayerStateVars(gameState.playerState)
            .withConstant("AMT", diceRollResult.toDouble())
            .eval(newFormula)
            .toInt()
        return finalResult <= entity.skill.currentValue
    }
}