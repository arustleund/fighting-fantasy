package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader

class SetOnPlayerDeathClosure(
    element: Element,
    closureLoader: ClosureLoader
) : Closure {

    private val onPlayerDeathClosure = closureLoader.loadClosureFromChildren(element)

    override fun execute(gameState: GameState): Boolean {
        gameState.playerState.onPlayerDeathClosure = onPlayerDeathClosure
        return true
    }
}