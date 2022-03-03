package rustleund.fightingfantasy.framework.base;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLUtil {

	public static Collection<Element> getChildElementsByName(Element parentElement, String childName) {
		Collection<Element> result = new ArrayList<>();
		NodeList elementsByTagName = parentElement.getElementsByTagNameNS("*", childName);
		for (int i = 0; i < elementsByTagName.getLength(); i++) {
			Element toCheck = (Element) elementsByTagName.item(i);
			if (toCheck.getParentNode().isSameNode(parentElement)) {
				result.add(toCheck);
			}
		}
		return result;
	}

	public static Element getChildElementByName(Element parentElement, String childName) {
		Collection<Element> childElementsByName = getChildElementsByName(parentElement, childName);
		if (childElementsByName.isEmpty()) {
			return null;
		}
		if (childElementsByName.size() > 1) {
			throw new IllegalArgumentException("There was more than one child for parent: " + parentElement.getLocalName() +
					" with child name: " + childName);
		}
		return childElementsByName.iterator().next();
	}
}
