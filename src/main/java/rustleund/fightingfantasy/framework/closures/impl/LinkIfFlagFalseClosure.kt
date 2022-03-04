package rustleund.fightingfantasy.framework.closures.impl

import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.closures.ClosureLoader
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.closures.Closure

class LinkIfFlagFalseClosure(
    element: Element,
    closureLoader: ClosureLoader,
    battleEffectsLoader: BattleEffectsLoader
) : Closure {

    private val testFlagPredicate = TestFlagPredicate(element)
    private val linkClosure = LinkClosure(element, closureLoader, battleEffectsLoader)
    private val setFlagToTrue = element.getAttribute("setFlagToTrue").toBoolean()

    override fun execute(gameState: GameState): Boolean {
        val flagTrue = testFlagPredicate.test(gameState)
        if (!flagTrue) {
            if (setFlagToTrue) {
                gameState.playerState.setFlag(testFlagPredicate.flagId, true)
            }
            return linkClosure.execute(gameState)
        }
        return true
    }
}