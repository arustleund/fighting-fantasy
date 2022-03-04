package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.intAttribute
import rustleund.fightingfantasy.framework.closures.Closure
import java.util.function.BiConsumer
import java.util.function.Function

class AdjustByAmountClosure(
    element: Element,
    private val currentValueGetter: Function<in GameState, out Int>,
    private val valueSetter: BiConsumer<in GameState, in Int>
) : Closure {

    private val amount = element.intAttribute("amount", 0)

    override fun execute(gameState: GameState): Boolean {
        val currentValue = currentValueGetter.apply(gameState)
        valueSetter.accept(gameState, currentValue + amount)
        return true
    }
}