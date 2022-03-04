package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import java.util.function.Predicate

/**
 * @param element An [Element] that represents the `<testFlag />` [Element]. Must contain an `id` attribute with a valid integer representing a flag id.
 */
class TestFlagPredicate(element: Element) : Predicate<GameState> {

    val flagId: Int = element.getAttribute("id").toInt()

    override fun test(input: GameState): Boolean {
        return input.playerState.getFlagValue(flagId)
    }
}