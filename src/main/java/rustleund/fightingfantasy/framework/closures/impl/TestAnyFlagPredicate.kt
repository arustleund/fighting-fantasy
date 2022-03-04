package rustleund.fightingfantasy.framework.closures.impl

import rustleund.fightingfantasy.framework.base.GameState
import org.w3c.dom.Element
import java.util.function.Predicate

/**
 * @param element An [Element] that represents an `<anyFlag></anyFlag>` element. Must contain an `ids` attribute with comma-separated integers representing flag ids.
 */
class TestAnyFlagPredicate(element: Element) : Predicate<GameState> {

    private val flagIds = element.getAttribute("ids").splitToSequence(',')
        .map { it.toInt() }.toList()

    override fun test(gameState: GameState): Boolean {
        val playerState = gameState.playerState
        return flagIds.any { playerState.getFlagValue(it) }
    }
}