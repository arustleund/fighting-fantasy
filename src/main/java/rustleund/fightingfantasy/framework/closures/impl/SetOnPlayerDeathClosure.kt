package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.hasChildElements
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.gamesave.SerializableClosure

class SetOnPlayerDeathClosure(
    private val onPlayerDeathClosure: Closure?
) : SerializableClosure {

    constructor(element: Element, closureLoader: ClosureLoader) : this(
        onPlayerDeathClosure = if (element.hasChildElements()) {
            closureLoader.loadClosureFromChildren(element)
        } else null
    )

    override fun execute(gameState: GameState): Boolean {
        gameState.playerState.onPlayerDeathClosure = onPlayerDeathClosure
        return true
    }
}