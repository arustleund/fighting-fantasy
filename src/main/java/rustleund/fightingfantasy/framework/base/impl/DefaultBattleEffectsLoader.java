package rustleund.fightingfantasy.framework.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.base.BattleEffects;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class DefaultBattleEffectsLoader implements BattleEffectsLoader {

	private ClosureLoader closureLoader;

	public DefaultBattleEffectsLoader(ClosureLoader closureLoader) {
		this.closureLoader = closureLoader;
	}

	@Override
	public List<BattleEffects> loadAllBattleEffectsFromTag(Element baseTagElement) {
		List<BattleEffects> result = null;
		NodeList allEffectGroups = baseTagElement.getChildNodes();
		if (allEffectGroups.getLength() > 0) {
			result = new ArrayList<>();
			for (int i = 0; i < allEffectGroups.getLength(); i++) {
				Node effectGroup = allEffectGroups.item(i);
				if (effectGroup instanceof Element) {
					BattleEffects effects = new BattleEffects();
					loadBattleEffectsFromTag(effects, (Element) effectGroup);
					result.add(effects);
				}
			}
		}
		return result;
	}

	@Override
	public void loadBattleEffectsFromTag(BattleEffects battleEffects, Element effectsTag) {
		NodeList allEffects = effectsTag.getChildNodes();
		for (int i = 0; i < allEffects.getLength(); i++) {
			Node effectChild = allEffects.item(i);
			if (effectChild instanceof Element) {
				Element effectTag = (Element) effectChild;
				Closure effectsFromTag = this.closureLoader.loadClosureFromChildren(effectTag);
				try {
					PropertyUtils.setProperty(battleEffects, effectTag.getNodeName(), effectsFromTag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
