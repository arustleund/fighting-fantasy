package rustleund.fightingfantasy.framework.closures.impl

import org.apache.commons.beanutils.PropertyUtils
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.*
import java.util.function.Predicate


class TestStatPredicate @JvmOverloads constructor(
    private val comparison: Comparison,
    private val stat: String,
    private val useInitialValue: Boolean = false,
    private val modifiedByFormula: String? = null,
    private val entityStateRetriever: (GameState) -> AbstractEntityState = GameState::getPlayerState,
    private val attackStrengthRetriever: (GameState) -> AttackStrength? =
        { it.battleState?.currentAttackStrengths?.playerAttackStrength }
) : Predicate<GameState> {

    @JvmOverloads constructor(
        element: Element,
        entityStateRetriever: (GameState) -> AbstractEntityState = GameState::getPlayerState,
        attackStrengthRetriever: (GameState) -> AttackStrength? =
            { it.battleState?.currentAttackStrengths?.playerAttackStrength }
    ) : this(
        comparison = element.toComparison(),
        stat = element.getAttribute("stat"),
        useInitialValue = element.booleanAttribute("useInitialValue", false),
        modifiedByFormula = element.optionalAttribute("modifiedByFormula"),
        entityStateRetriever = entityStateRetriever,
        attackStrengthRetriever = attackStrengthRetriever
    )

    private val keval = keval()

    override fun test(gameState: GameState): Boolean {
        return runCatching {
            val entityStateToTest = entityStateRetriever(gameState)
            val statValue = getStatValue(entityStateToTest, gameState)
            statValue.testAgainst(comparison)
        }.onFailure { it.printStackTrace() }.getOrDefault(false)
    }

    private fun getStatValue(entityStateToTest: AbstractEntityState, gameState: GameState): Int? {
        val rawValue = when (stat) {
            "attackStrength" -> attackStrengthRetriever(gameState)?.total
            "hitCount" -> gameState.battleState?.playerHitCount
            "battleRound" -> gameState.battleState?.battleRound
            else -> getNonAttackStrengthStatValue(entityStateToTest)
        }
        return modifiedByFormula?.let { formula ->
            rawValue?.let { rv -> keval.withConstant("AMT", rv.toDouble()).eval(formula).toInt() }
        } ?: rawValue
    }

    private fun getNonAttackStrengthStatValue(entityStateToTest: AbstractEntityState): Int {
        val statScale = PropertyUtils.getProperty(entityStateToTest, stat) as Scale
        return if (useInitialValue) statScale.upperBound!! else statScale.currentValue
    }
}