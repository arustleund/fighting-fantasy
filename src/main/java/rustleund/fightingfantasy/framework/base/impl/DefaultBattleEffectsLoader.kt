package rustleund.fightingfantasy.framework.base.impl

import org.apache.commons.beanutils.PropertyUtils
import org.w3c.dom.Element
import rustleund.fightingfantasy.framework.base.BattleEffects
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader
import rustleund.fightingfantasy.framework.base.asElementSequence
import rustleund.fightingfantasy.framework.closures.ClosureLoader

class DefaultBattleEffectsLoader(private val closureLoader: ClosureLoader) : BattleEffectsLoader {

    override fun loadAllBattleEffectsFromTag(baseTagElement: Element) = baseTagElement.childNodes
        .asElementSequence()
        .map {
            val effects = BattleEffects()
            loadBattleEffectsFromTag(effects, it)
            effects
        }.toList()

    override fun loadBattleEffectsFromTag(battleEffects: BattleEffects, effectsTag: Element) {
        effectsTag.childNodes.asElementSequence().forEach {
            val effectsFromTag = closureLoader.loadClosureFromChildren(it)
            PropertyUtils.setProperty(battleEffects, it.localName, effectsFromTag)
        }
    }
}