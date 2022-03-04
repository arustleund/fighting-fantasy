package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure
import java.util.function.Predicate

class PredicateTextClosure(e: Element, private val predicate: Predicate<GameState>) : Closure {

    private val success = DisplayTextClosure(e.getAttribute("success").toInt())
    private val failure = DisplayTextClosure(e.getAttribute("failure").toInt())

    override fun execute(gameState: GameState) =
        (if (predicate.test(gameState)) success else failure).execute(gameState)
}