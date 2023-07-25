package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

class AddBattleEffectsToNextBattleRoundClosure(element: Element, battleEffectsLoader: BattleEffectsLoader) : Closure {

    private val battleEffects = battleEffectsLoader.loadAllBattleEffectsFromTag(element)

    override fun execute(gameState: GameState): Boolean {
        gameState.battleState?.addBattleEffectsForNextRound(battleEffects)
        return true
    }
}