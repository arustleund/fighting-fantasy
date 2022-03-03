/*
 * Created on Oct 25, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import rustleund.fightingfantasy.framework.base.ItemUtil
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.PlayerState
import rustleund.fightingfantasy.framework.closures.Closure

/**
 * @author rustlea
 */
class InitPlayerStateClosure(private val itemUtil: ItemUtil) : Closure {

    override fun execute(gameState: GameState): Boolean {
        gameState.playerState = PlayerState(
            gameState.playerState.name,
            listOf(
                itemUtil.getItem(0),
                itemUtil.getItem(1),
                itemUtil.getItem(2),
                itemUtil.getItem(3)
            )
        )
        return true
    }
}