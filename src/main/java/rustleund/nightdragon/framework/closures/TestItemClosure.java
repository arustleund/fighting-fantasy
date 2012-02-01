/*
 * Created on Oct 9, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.apache.commons.collections.Closure;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestItemClosure extends AbstractCommand {

	private int itemId = -1;

	private Closure hasItem = null;

	private Closure doesNotHaveItem = null;

	public TestItemClosure(Element element) {
		
		this.executeSuccessful = true;

		this.itemId = Integer.parseInt(element.getAttribute("id"));

		Element hasItemElement = (Element) element.getElementsByTagName("hasItem").item(0);
		this.hasItem = AbstractCommandLoader.loadChainedClosure(hasItemElement);

		Element doesNotHaveItemElement = (Element) element.getElementsByTagName("doesNotHaveItem").item(0);
		this.doesNotHaveItem = AbstractCommandLoader.loadChainedClosure(doesNotHaveItemElement);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(Object arg0) {

		GameState gameState = (GameState) arg0;

		if (gameState.getPlayerState().itemCount(itemId) == 0) {
			doesNotHaveItem.execute(gameState);
		} else {
			hasItem.execute(gameState);
		}

	}
}