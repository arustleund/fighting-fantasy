/*
 * Created on Oct 27, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.intAttribute
import rustleund.fightingfantasy.framework.closures.Closure

/**
 * @author rustlea
 */
class MustEatMealClosure(
    element: Element,
    private val closureLoader: ClosureLoader,
    private val battleEffectsLoader: BattleEffectsLoader
) : Closure {

    private val number = element.intAttribute("amount", 1)

    override fun execute(gameState: GameState): Boolean {
        val playerState = gameState.playerState
        for (i in 0 until number) {
            if (playerState.provisions.currentValue > 0) {
                playerState.provisions.adjustCurrentValueNoException(-1)
            } else {
                gameState.message = "You lost 2 Stamina because you were out of provisions"
                playerState.stamina.adjustCurrentValueNoException(-2)
                if (playerState.isDead) {
                    LinkClosure("0", closureLoader, battleEffectsLoader).execute(gameState)
                    return false
                }
            }
        }
        return true
    }
}