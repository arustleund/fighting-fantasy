/*
 * Created on Jun 27, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.apache.commons.beanutils.PropertyUtils
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.util.DiceRoller
import java.lang.IndexOutOfBoundsException
import kotlin.math.ceil
import kotlin.math.floor

/**
 * @author rustlea
 */
open class AdjustScaleClosure(
    element: Element,
    private val closureLoader: ClosureLoader,
    private val battleEffectsLoader: BattleEffectsLoader
) : Closure {

    private var scaleName: String? = null
    private val stringAmount: String
    private val promptOnFail: Boolean
    private val useAmountAsValue: Boolean
    private val useAmountAsPercent: Boolean
    private val round: String
    private val adjustInitialValue: Boolean
    private val rollDiceAmount: Int?
    private val negate: Boolean

    init {
        scaleName = element.getAttribute("type")
        stringAmount = element.getAttribute("amount")
        promptOnFail = element.booleanAttribute("promptOnFail")
        useAmountAsValue = element.booleanAttribute("useAmountAsValue")
        useAmountAsPercent = element.booleanAttribute("useAmountAsPercent")
        round = element.getAttribute("round")
        adjustInitialValue = element.booleanAttribute("adjustInitialValue")
        rollDiceAmount = element.optionalIntAttribute("rollDiceAmount")
        negate = element.booleanAttribute("negate")
    }

    override fun execute(gameState: GameState): Boolean {
        val scale = runCatching { PropertyUtils.getProperty(entity(gameState), scaleName) as Scale }
            .onFailure { it.printStackTrace() }
            .getOrNull() ?: return false

        var amountToAdjust: Int
        if (useAmountAsPercent) {
            requireNotNull(scale.upperBound) { "Scale must have an upper bound" }
            val percentAdjustment = stringAmount.toDouble()
            val percentResult = scale.upperBound.toInt() * percentAdjustment
            amountToAdjust = if ("up".equals(round, ignoreCase = true)) {
                ceil(percentResult).toInt()
            } else if ("down".equals(round, ignoreCase = true)) {
                floor(percentResult).toInt()
            } else {
                percentResult.toInt()
            }
        } else {
            amountToAdjust = stringAmount.toInt()
            if (useAmountAsValue) {
                if (adjustInitialValue) {
                    if (scale.upperBound != null) {
                        amountToAdjust -= scale.upperBound.toInt()
                    }
                } else {
                    amountToAdjust -= scale.currentValue
                }
            } else if (rollDiceAmount != null) {
                amountToAdjust += DiceRoller.rollDice(rollDiceAmount)
            }
        }
        if (negate) {
            amountToAdjust *= -1
        }
        if (promptOnFail) {
            return try {
                scale.adjustCurrentValue(amountToAdjust)
                true
            } catch (e1: IndexOutOfBoundsException) {
                gameState.message = "You cannot perform this action"
                false
            }
        }
        if (adjustInitialValue) {
            scale.adjustUpperBound(amountToAdjust)
        } else {
            scale.adjustCurrentValueNoException(amountToAdjust)
        }
        if (gameState.playerState.isDead) {
            LinkClosure("0", closureLoader, battleEffectsLoader).execute(gameState)
        }
        return true
    }

    protected open fun entity(gameState: GameState): AbstractEntityState? {
        return gameState.playerState
    }
}