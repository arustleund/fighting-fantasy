package rustleund.fightingfantasy.framework.base.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.Item;
import rustleund.fightingfantasy.framework.base.ItemUtil;
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
			Document itemDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(itemConfiguration);
			NodeList itemTags = itemDocument.getElementsByTagName("item");
			for (int i = 0; i < itemTags.getLength(); i++) {
				loadItemTag((Element) itemTags.item(i));
			}
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
		NodeList onUseElements = itemElement.getElementsByTagName("onUse");
		if (onUseElements.getLength() == 1) {
			item.setUseItem(this.closureLoader.loadClosureFromChildren((Element) onUseElements.item(0)));
		}
	}

	private void loadBattleEffects(Element itemElement, Item item) {
		// TODO Auto-generated method stub

	}

	@Override
	public Item getItem(int itemId) {
		return this.items.get(itemId);
	}
}
