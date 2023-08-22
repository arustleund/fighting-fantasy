package rustleund.fightingfantasy.framework.closures.impl

import com.notkamui.keval.Keval
import rustleund.fightingfantasy.framework.base.PlayerState
import rustleund.fightingfantasy.framework.util.DiceRoller
import kotlin.math.ceil
import kotlin.math.floor

fun keval(diceRoller: (Int) -> Int = { DiceRoller.rollDice(it) }) = Keval {
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

    function {
        name = "max"
        arity = 2
        implementation = { maxOf(it[0], it[1]) }
    }

    function {
        name = "min"
        arity = 2
        implementation = { minOf(it[0], it[1]) }
    }
}

private val playerStateMappings = listOf(
    "gold" to PlayerState::getGold,
    "stamina" to PlayerState::getStamina,
    "skill" to PlayerState::getSkill,
    "time" to PlayerState::getTime,
    "luck" to PlayerState::getLuck,
    "honor" to PlayerState::getHonor,
    "nemesis" to PlayerState::getNemesis,
    "provisions" to PlayerState::getProvisions
)
    .flatMap {
        sequenceOf<Pair<String, (PlayerState) -> Int?>>(
            "player_${it.first}_currentValue" to { ps -> it.second(ps).currentValue },
            "player_${it.first}_upperBound" to { ps -> it.second(ps).upperBound },
        )
    }

/**
 * Add constants for scale values for the given [PlayerState].
 * Constant names will be in the form: `player_gold_currentValue` and
 * `player_stamina_upperBound`
 */
fun Keval.withPlayerStateVars(playerState: PlayerState): Keval {
    playerStateMappings.forEach { mapping ->
        mapping.second(playerState)?.toDouble()?.let { value -> withConstant(mapping.first, value) }
    }
    return this
}

fun String?.portOldFormula(): String? = this?.let { f ->
    f.toIntOrNull()?.let { i -> "AMT+$i" } ?: f
}