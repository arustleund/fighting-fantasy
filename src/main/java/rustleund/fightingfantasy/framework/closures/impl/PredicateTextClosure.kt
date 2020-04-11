package rustleund.fightingfantasy.framework.closures.impl

import com.google.common.base.Predicate
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

class PredicateTextClosure(e: Element, private val predicate: Predicate<GameState>) : Closure {

    private val success = DisplayTextClosure(e.getAttribute("success").toInt())
    private val failure = DisplayTextClosure(e.getAttribute("failure").toInt())

    override fun execute(gameState: GameState): Boolean {
        return if (predicate.apply(gameState)) success.execute(gameState) else failure.execute(gameState)
    }
}