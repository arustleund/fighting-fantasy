package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

class AddBattleEffectsForNextBattleClosure(element: Element, battleEffectsLoader: BattleEffectsLoader) : Closure {

    private val nextBattleBattleEffects = battleEffectsLoader.loadAllBattleEffectsFromTag(element)

    override fun execute(gameState: GameState): Boolean {
        gameState.playerState.nextBattleBattleEffects =
            gameState.playerState.nextBattleBattleEffects.orEmpty() + nextBattleBattleEffects
        return true
    }
}