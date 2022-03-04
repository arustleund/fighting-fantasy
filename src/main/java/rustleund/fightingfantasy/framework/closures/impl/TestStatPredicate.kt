package rustleund.fightingfantasy.framework.closures.impl

import org.apache.commons.beanutils.PropertyUtils
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.AbstractEntityState
import rustleund.fightingfantasy.framework.base.Scale
import rustleund.fightingfantasy.framework.base.booleanAttribute
import java.util.function.Predicate


class TestStatPredicate @JvmOverloads constructor(
    element: Element,
    private val entityStateRetriever: (GameState) -> AbstractEntityState = GameState::getPlayerState,
    private val attackStrengthRetriever: (GameState) -> Int =
        { it.battleState.currentAttackStrengths.playerAttackStrength.total }
) : Predicate<GameState> {

    private val comparison = element.toComparison()
    private val stat = element.getAttribute("stat")
    private val useInitialValue = element.booleanAttribute("useInitialValue", false)

    override fun test(gameState: GameState): Boolean {
        return runCatching {
            val entityStateToTest = entityStateRetriever(gameState)
            val statValue = getStatValue(entityStateToTest, gameState)
            statValue.testAgainst(comparison)
        }.onFailure { it.printStackTrace() }.getOrDefault(false)
    }

    private fun getStatValue(entityStateToTest: AbstractEntityState, gameState: GameState): Int {
        return when (stat) {
            "attackStrength" -> attackStrengthRetriever(gameState)
            "hitCount" -> gameState.battleState.playerHitCount
            "battleRound" -> gameState.battleState.battleRound
            else -> getNonAttackStrengthStatValue(entityStateToTest)
        }
    }

    private fun getNonAttackStrengthStatValue(entityStateToTest: AbstractEntityState): Int {
        val statScale = PropertyUtils.getProperty(entityStateToTest, stat) as Scale
        return if (useInitialValue) statScale.upperBound!! else statScale.currentValue
    }
}