/*
 * Created on Jul 5, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.closures.Closure;


/**
 * @author rustlea
 */
public abstract class AbstractClosure implements Closure {

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