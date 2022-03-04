package rustleund.fightingfantasy.framework.closures

import rustleund.fightingfantasy.framework.base.GameState

/**
 * @see [execute]
 */
interface Closure {

    /**
     * Execute a game task.
     *
     * @param gameState The current [GameState]
     *
     * @return `true` if the execution was successful, `false` otherwise
     */
    fun execute(gameState: GameState): Boolean
}