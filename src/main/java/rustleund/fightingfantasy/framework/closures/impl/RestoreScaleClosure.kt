/*
 * Created on Oct 15, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.apache.commons.beanutils.PropertyUtils
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.Scale
import rustleund.fightingfantasy.framework.closures.Closure

/**
 * @author rustlea
 */
class RestoreScaleClosure(element: Element) : Closure {

    private val scaleName = element.getAttribute("type")

    override fun execute(gameState: GameState): Boolean {
        (PropertyUtils.getProperty(gameState.playerState, scaleName) as Scale).restorePreviousValue()
        return true
    }
}