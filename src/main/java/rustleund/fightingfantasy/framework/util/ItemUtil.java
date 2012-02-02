/*
 * Created on Jul 12, 2004
 */
package rustleund.fightingfantasy.framework.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.fightingfantasy.framework.base.Item;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

/**
 * @author rustlea
 */
public class ItemUtil {

	private ClosureLoader closureLoader;

	private Map<Integer, Item> items = null;

	private static ItemUtil instance = null;

	private ItemUtil() {
		items = new HashMap<Integer, Item>();
	}

	public void init(ClosureLoader closureLoader) {
		this.closureLoader = closureLoader;

		try {
			Document itemDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ClassLoader.getSystemResourceAsStream("nightdragon/config/items.xml"));
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
		item.setPrice(new Integer(itemElement.getAttribute("price")));
		if (itemElement.hasAttribute("limit")) {
			item.setLimit(new Integer(itemElement.getAttribute("limit")));
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

	public static ItemUtil getInstance() {
		if (instance == null) {
			instance = new ItemUtil();
		}
		return instance;
	}

	public Item getItem(int itemId) {
		return items.get(new Integer(itemId));
	}

}