package rustleund.fightingfantasy.framework.closures.impl

import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

class ClearPoisonDamageClosure : Closure {

    override fun execute(gameState: GameState): Boolean {
        gameState.playerState.clearPoisonDamage()
        return true
    }
}