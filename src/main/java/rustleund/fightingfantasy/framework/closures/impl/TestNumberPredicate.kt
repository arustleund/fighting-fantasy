package rustleund.fightingfantasy.framework.closures.impl

import rustleund.fightingfantasy.framework.base.GameState
import org.w3c.dom.Element
import java.util.function.Function
import java.util.function.Predicate

class TestNumberPredicate(
    element: Element,
    private val gameStateToNumberFunction: Function<in GameState, out Int>
) : Predicate<GameState> {

    private val comparison = element.toComparison()

    override fun test(input: GameState) = gameStateToNumberFunction.apply(input).testAgainst(comparison)
}