/*
 * Created on Jul 12, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.Closure

class AddItemClosure
@JvmOverloads constructor(
    private val itemId: Int,
    itemUtil: ItemUtil,
    private val quantity: Int = 1,
    private val priceOverride: Int? = null,
    private val pageLimit: Int? = null
) : Closure {

    private var pageBuys = 0
    private val item = itemUtil.getItem(itemId)
    private val totalPrice = (priceOverride ?: item.defaultPrice) * quantity

    constructor(addItemElement: Element, itemUtil: ItemUtil) : this(
        itemId = addItemElement.optionalIntAttribute("id") ?: throw IllegalArgumentException("Missing id"),
        itemUtil = itemUtil,
        quantity = addItemElement.intAttribute("quantity", 1),
        priceOverride = addItemElement.optionalIntAttribute("price"),
        pageLimit = addItemElement.optionalIntAttribute("pageLimit")
    )

    override fun execute(gameState: GameState): Boolean {
        val playerState = gameState.playerState
        val pageState = gameState.pageState
        if (item.hasLimit() && playerState.itemCount(itemId) + quantity > item.limit) {
            gameState.message = "Buying the ${item.name} would put you above the maximum amount allowed (${item.limit})"
            return false
        }
        if (pageLimit != null && pageBuys + quantity > pageLimit) {
            gameState.message = "This location cannot sell any more of the ${item.name}"
            return false
        }
        if (pageState.hasKeepMinimumForScale("gold") && playerState.gold.currentValue - totalPrice < pageState.getKeepMinimumForScale(
                "gold"
            )
        ) {
            gameState.message =
                "Buying the ${item.name} would put you below the minimum of ${pageState.getKeepMinimumForScale("gold")} Gold Pieces"
            return false
        }
        if (playerState.gold.currentValue < totalPrice) {
            gameState.message = "You do not have sufficient Gold to buy the ${item.name}"
            return false
        }
        playerState.gold.adjustCurrentValue(totalPrice * -1)
        repeat(quantity) { playerState.addItem(item) }
        pageBuys += quantity
        return true
    }
}