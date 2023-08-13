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
    private val quantityFormula: String? = null,
    private val priceOverride: Int? = null,
    private val pageLimit: Int? = null
) : Closure {

    private var pageBuys = 0
    private val item = itemUtil.getItem(itemId)
    private val keval = keval()

    constructor(addItemElement: Element, itemUtil: ItemUtil) : this(
        itemId = addItemElement.optionalIntAttribute("id") ?: throw IllegalArgumentException("Missing id"),
        itemUtil = itemUtil,
        quantity = addItemElement.intAttribute("quantity", 1),
        quantityFormula = addItemElement.optionalAttribute("quantityFormula"),
        priceOverride = addItemElement.optionalIntAttribute("price"),
        pageLimit = addItemElement.optionalIntAttribute("pageLimit")
    )

    override fun execute(gameState: GameState): Boolean {
        val playerState = gameState.playerState
        val pageState = gameState.pageState

        val quantityToUse = quantityFormula?.let { keval.withConstant("AMT", quantity.toDouble()).eval(it).toInt() } ?: quantity
        val totalPrice = (priceOverride ?: item.defaultPrice) * quantityToUse

        if (item.hasLimit() && playerState.itemCount(itemId) + quantityToUse > item.limit) {
            gameState.message = "Buying the ${item.name} would put you above the maximum amount allowed (${item.limit})"
            return false
        }
        if (pageLimit != null && pageBuys + quantityToUse > pageLimit) {
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
        repeat(quantityToUse) { playerState.addItem(item) }
        pageBuys += quantityToUse
        return true
    }
}