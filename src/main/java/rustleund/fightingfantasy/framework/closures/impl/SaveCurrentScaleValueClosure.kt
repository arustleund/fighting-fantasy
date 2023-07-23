package rustleund.fightingfantasy.framework.closures.impl

import org.apache.commons.beanutils.PropertyUtils
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.Scale
import rustleund.fightingfantasy.framework.closures.Closure

class SaveCurrentScaleValueClosure(
    element: Element
) : Closure {

    private val scaleName = element.getAttribute("type")
    private val label = element.getAttribute("label")

    override fun execute(gameState: GameState): Boolean {
        val scale = PropertyUtils.getProperty(gameState.playerState, scaleName) as Scale
        scale.saveCurrentValue(label)
        return true
    }
}