package rustleund.fightingfantasy.framework.closures.impl

import com.notkamui.keval.Keval
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