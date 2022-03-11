/*
 * Created on Jun 27, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl

import com.notkamui.keval.Keval
import org.apache.commons.beanutils.PropertyUtils
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import rustleund.fightingfantasy.framework.closures.Closure
import rustleund.fightingfantasy.framework.util.DiceRoller
import java.lang.IndexOutOfBoundsException
import kotlin.math.ceil
import kotlin.math.floor

class AdjustScaleClosure @JvmOverloads constructor(
    element: Element,
    private val entityRetriever: (GameState) -> AbstractEntityState? = { it.playerState },
    private val diceRoller: (Int) -> Int = { DiceRoller.rollDice(it) }
) : Closure {

    private val scaleName = element.getAttribute("type")
    private val stringAmount = element.getAttribute("amount")
    private val promptOnFail = element.booleanAttribute("promptOnFail")
    private val useAmountAsValue = element.booleanAttribute("useAmountAsValue")
    private val useAmountAsPercent = element.booleanAttribute("useAmountAsPercent")
    private val round = element.getAttribute("round")
    private val adjustInitialValue = element.booleanAttribute("adjustInitialValue")
    private val rollDiceAmount = element.optionalIntAttribute("rollDiceAmount")
    private val formula = element.optionalAttribute("formula")
    private val negate = element.booleanAttribute("negate")

    private val keval = Keval {
        includeDefault()

        function {
            name = "roll"
            arity = 1
            implementation = { diceRoller(it[0].toInt()).toDouble() }
        }

        function {
            name = "floor"
            arity = 1
            implementation = { floor(it[0]) }
        }

        function {
            name = "ceil"
            arity = 1
            implementation = { ceil(it[0]) }
        }
    }

    override fun execute(gameState: GameState): Boolean {
        val scale = runCatching { PropertyUtils.getProperty(entityRetriever(gameState), scaleName) as Scale }
            .onFailure { it.printStackTrace() }
            .getOrNull() ?: return false

        val amountToAdjust: Int = getAmountToAdjustBy(scale)
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
            DisplayTextClosure("You have died, and you adventure ends here").execute(gameState)
            //LinkClosure("0", closureLoader, battleEffectsLoader).execute(gameState)
        }
        return true
    }

    private fun getAmountToAdjustBy(scale: Scale): Int {
        var amountToAdjust: Int
        if (useAmountAsPercent) {
            amountToAdjust = getAmountToAdjustByAsPercent(scale)
        } else {
            amountToAdjust = stringAmount.toInt()
            if (useAmountAsValue) {
                amountToAdjust -= if (adjustInitialValue) scale.upperBound ?: 0 else scale.currentValue
            } else if (rollDiceAmount != null) {
                amountToAdjust += diceRoller(rollDiceAmount)
            }
        }
        if (formula != null) {
            amountToAdjust = keval.withConstant("AMT", amountToAdjust.toDouble()).eval(formula).toInt()
        }
        return if (negate) amountToAdjust * -1 else amountToAdjust
    }

    private fun getAmountToAdjustByAsPercent(scale: Scale): Int {
        val upperBound = requireNotNull(scale.upperBound) { "Scale must have an upper bound" }
        val percentAdjustment = stringAmount.toDouble()
        val percentResult = upperBound * percentAdjustment
        return when (round.lowercase()) {
            "up" -> ceil(percentResult).toInt()
            "down" -> floor(percentResult).toInt()
            else -> percentResult.toInt()
        }
    }
}