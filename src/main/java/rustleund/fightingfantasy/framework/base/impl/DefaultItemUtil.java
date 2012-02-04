package rustleund.fightingfantasy.framework.base.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.base.Item;
import rustleund.fightingfantasy.framework.base.ItemUtil;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class DefaultItemUtil implements ItemUtil {

	private ClosureLoader closureLoader;

	private Map<Integer, Item> items;

	public DefaultItemUtil(ClosureLoader closureLoader) {
		this.closureLoader = closureLoader;
	}

	@Override
	public void init(File itemConfiguration) {
		this.items = new HashMap<Integer, Item>();

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
		Item item = new Item();
		item.setId(new Integer(itemElement.getAttribute("id")));
		item.setName(itemElement.getAttribute("name"));
		item.setPrice(Integer.valueOf(itemElement.getAttribute("price")));
		if (itemElement.hasAttribute("limit")) {
			item.setLimit(Integer.valueOf(itemElement.getAttribute("limit")));
		}
		if (itemElement.hasChildNodes()) {
			item.setUseItem(this.closureLoader.loadClosureFromChildren(itemElement));
		}
		if (itemElement.hasAttribute("canUseInBattle")) {
			item.setCanUseInBattle(Boolean.getBoolean(itemElement.getAttribute("canUseInBattle")));
		} else {
			item.setCanUseInBattle(true);
		}
		items.put(item.getId(), item);
	}

	@Override
	public Item getItem(int itemId) {
		return this.items.get(itemId);
	}

}
