package rustleund.fightingfantasy.framework.base.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffects;
import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.Item;
import rustleund.fightingfantasy.framework.base.ItemUtil;
import rustleund.fightingfantasy.framework.base.XMLUtil;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class DefaultItemUtil implements ItemUtil {

	private ClosureLoader closureLoader;
	private BattleEffectsLoader battleEffectsLoader;

	private Map<Integer, Item> items;

	public DefaultItemUtil(ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		this.closureLoader = closureLoader;
		this.battleEffectsLoader = battleEffectsLoader;
	}

	@Override
	public void init(File itemConfiguration) {
		this.items = new HashMap<>();

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			Document itemDocument = documentBuilderFactory.newDocumentBuilder().parse(itemConfiguration);
			XMLUtil.getChildElementsByName(itemDocument.getDocumentElement(), "item").forEach(this::loadItemTag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadItemTag(Element itemElement) {
		Integer id = Integer.valueOf(itemElement.getAttribute("id"));
		String itemName = itemElement.getAttribute("name");
		Integer defaultPrice = Integer.valueOf(itemElement.getAttribute("price"));
		Item item = new Item(id, itemName, defaultPrice);
		if (itemElement.hasAttribute("limit")) {
			item.setLimit(Integer.valueOf(itemElement.getAttribute("limit")));
		}
		loadOnUse(itemElement, item);
		loadBattleEffects(itemElement, item);
		if (itemElement.hasAttribute("canUseInBattle")) {
			item.setCanUseInBattle(Boolean.valueOf(itemElement.getAttribute("canUseInBattle")));
		} else {
			item.setCanUseInBattle(true);
		}
		items.put(item.getId(), item);
	}

	private void loadOnUse(Element itemElement, Item item) {
		Element onUseElement = XMLUtil.getChildElementByName(itemElement, "onUse");
		if (onUseElement != null) {
			item.setUseItem(this.closureLoader.loadClosureFromChildren(onUseElement));
		}
	}

	private void loadBattleEffects(Element itemElement, Item item) {
		Element battleEffectsElement = XMLUtil.getChildElementByName(itemElement, "battleEffects");
		if (battleEffectsElement != null) {
			BattleEffects battleEffects = new BattleEffects();
			this.battleEffectsLoader.loadBattleEffectsFromTag(battleEffects, battleEffectsElement);
			item.setBattleEffects(battleEffects);
		}
	}

	@Override
	public Item getItem(int itemId) {
		return this.items.get(itemId);
	}
}
