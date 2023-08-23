/*
 * Created on Oct 15, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.util.DiceRoller

/**
 * @author rustlea
 */
class RollDiceClosure(element: Element, closureLoader: ClosureLoader) : Closure {

    private val number: Int
    private val rollMappings: Map<Int, Closure>

    init {
        number = element.intAttribute("number", 1)
        rollMappings = element.getChildElementsByName("doActions")
            .flatMap {
                val actionsForElement = closureLoader.loadClosureFromChildren(it)
                it.getAttribute("rolls").splitToSequence(',')
                    .map { roll -> roll.toInt() to actionsForElement }
            }.toMap()
    }

    override fun execute(gameState: GameState): Boolean {
        val diceRollResult = DiceRoller.rollDice(number)
        rollMappings[diceRollResult]?.execute(gameState)
        return true
    }
}