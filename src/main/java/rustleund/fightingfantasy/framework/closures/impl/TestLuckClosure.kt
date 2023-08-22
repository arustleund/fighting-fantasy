/*
 * Created on Oct 10, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.optionalAttribute
import rustleund.fightingfantasy.framework.closures.Closure

/**
 * @author rustlea
 */
class TestLuckClosure(element: Element, closureLoader: ClosureLoader) : Closure {

    private val trueClosure: Closure = closureLoader.loadClosureFromChild(element, "successful")
    private val falseClosure: Closure = closureLoader.loadClosureFromChild(element, "unsuccessful")
    private val diceRollAdjustmentFormula: String? = element.optionalAttribute("diceRollAdjustment")

    override fun execute(gameState: GameState): Boolean {
        val playerState = gameState.playerState
        val lucky = playerState.testLuck(diceRollAdjustmentFormula.portOldFormula())
        return (if (lucky) trueClosure else falseClosure).execute(gameState)
    }
}