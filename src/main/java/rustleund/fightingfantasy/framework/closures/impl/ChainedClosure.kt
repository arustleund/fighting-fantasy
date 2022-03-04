package rustleund.fightingfantasy.framework.closures.impl

import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

class ChainedClosure(private val closures: List<Closure> = listOf()) : Closure {

    override fun execute(gameState: GameState) = closures.all { it.execute(gameState) }

    override fun toString(): String {
        return closures.toString()
    }
}