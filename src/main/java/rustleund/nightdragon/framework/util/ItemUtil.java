/*
 * Created on Jul 12, 2004
 */
package rustleund.nightdragon.framework.util;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rustleund.nightdragon.framework.Item;

/**
 * @author rustlea
 */
public class ItemUtil {

	private Map items = null;

	private static ItemUtil instance = null;

	private ItemUtil() {
		items = new HashMap();
	}

	public void init() {
		try {
			URL resource = ClassLoader.getSystemResource("config/items.xml");
			File itemFile = new File(resource.toURI());
			Document itemDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(itemFile);
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
			item.setUseItem(AbstractCommandLoader.loadChainedClosure(itemElement));
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
		return (Item) items.get(new Integer(itemId));
	}

}