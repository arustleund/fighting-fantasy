package rustleund.fightingfantasy.framework.base;

import java.util.List;

import org.w3c.dom.Element;

public interface BattleEffectsLoader {

	List<BattleEffects> loadAllBattleEffectsFromTag(Element baseTagElement);

	void loadBattleEffectsFromTag(BattleEffects battleEffects, Element effectsTag);

}
