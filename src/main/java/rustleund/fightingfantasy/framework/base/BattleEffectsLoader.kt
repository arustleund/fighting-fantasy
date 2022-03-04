package rustleund.fightingfantasy.framework.base

import org.w3c.dom.Element

interface BattleEffectsLoader {

    fun loadAllBattleEffectsFromTag(baseTagElement: Element): List<BattleEffects>

    fun loadBattleEffectsFromTag(battleEffects: BattleEffects, effectsTag: Element)
}