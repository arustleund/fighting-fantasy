/*
 * Created on Oct 15, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl

import org.apache.commons.beanutils.PropertyUtils
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.GameState
import rustleund.fightingfantasy.framework.base.Scale
import rustleund.fightingfantasy.framework.base.optionalAttribute
import rustleund.fightingfantasy.gamesave.SerializableClosure

/**
 * @author rustlea
 */
class RestoreScaleClosure(
    private val scaleName: String,
    private val label: String?
) : SerializableClosure {

    constructor(element: Element) : this(
        scaleName = element.getAttribute("type"),
        label = element.optionalAttribute("label")
    )

    override fun execute(gameState: GameState): Boolean {
        val scale = PropertyUtils.getProperty(gameState.playerState, scaleName) as Scale
        if (label == null) scale.restorePreviousValue() else scale.restoreSavedValue(label)
        return true
    }
}