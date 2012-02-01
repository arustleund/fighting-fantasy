/*
 * Created on Jul 5, 2004
 */
package rustleund.nightdragon.framework;

import org.w3c.dom.Element;

/**
 * @author rustlea
 */
public abstract class AbstractCommand implements Closure {

	protected boolean executeSuccessful = true;

	public abstract void execute(GameState gameState);

	public boolean executeWasSuccessful() {
		return executeSuccessful;
	}

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