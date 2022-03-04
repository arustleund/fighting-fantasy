package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import java.util.function.Predicate

/**
 * @param element A `<testItem />` [Element] with an `id` attribute containing an item id
 */
class TestItemPredicate(element: Element) : Predicate<GameState> by TestNumberPredicate(element, {
    it.playerState.itemCount(element.getAttribute("id").toInt())
})