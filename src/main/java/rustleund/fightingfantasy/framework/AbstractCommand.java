/*
 * Created on Jul 5, 2004
 */
package rustleund.fightingfantasy.framework;

import org.w3c.dom.Element;

/**
 * @author rustlea
 */
public abstract class AbstractCommand implements Closure {

	protected boolean attributeValue(Element element, String attribute) {
		return attributeValue(element, attribute, false);
	}

	protected boolean attributeValue(Element element, String attribute, boolean defaultValue) {
		if (element.hasAttribute(attribute)) {
			return element.getAttribute(attribute).equalsIgnoreCase("true");
		}
		return defaultValue;
	}

}