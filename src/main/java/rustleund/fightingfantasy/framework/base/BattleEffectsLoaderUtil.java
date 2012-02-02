package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.closures.impl.ChainedClosure;
import rustleund.fightingfantasy.framework.closures.impl.DefaultClosureLoader;

public class BattleEffectsLoaderUtil {

	public static List<BattleEffects> loadAllBattleEffectsFromTag(Element baseTagElement) {
		List<BattleEffects> result = null;
		NodeList allEffectGroups = baseTagElement.getChildNodes();
		if (allEffectGroups.getLength() > 0) {
			result = new ArrayList<BattleEffects>();
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

	public static void loadBattleEffectsFromTag(Object objectToSetEffectsOn, Element effectsTag) {
		NodeList allEffects = effectsTag.getChildNodes();
		for (int i = 0; i < allEffects.getLength(); i++) {
			Node effectChild = allEffects.item(i);
			if (effectChild instanceof Element) {
				Element effectTag = (Element) effectChild;
				ChainedClosure effectsFromTag = DefaultClosureLoader.loadChainedClosure(effectTag);
				try {
					PropertyUtils.setProperty(objectToSetEffectsOn, effectTag.getNodeName(), effectsFromTag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
