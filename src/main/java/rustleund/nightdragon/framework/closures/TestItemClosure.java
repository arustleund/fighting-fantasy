/*
 * Created on Oct 9, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Command;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestItemClosure extends AbstractCommand {

	private int itemId = -1;

	private Command hasItem = null;

	private Command doesNotHaveItem = null;

	public TestItemClosure(Element element) {

		this.executeSuccessful = true;

		this.itemId = Integer.parseInt(element.getAttribute("id"));

		Element hasItemElement = (Element) element.getElementsByTagName("hasItem").item(0);
		this.hasItem = AbstractCommandLoader.loadChainedClosure(hasItemElement);

		Element doesNotHaveItemElement = (Element) element.getElementsByTagName("doesNotHaveItem").item(0);
		this.doesNotHaveItem = AbstractCommandLoader.loadChainedClosure(doesNotHaveItemElement);

	}

	public void execute(GameState gameState) {
		if (gameState.getPlayerState().itemCount(itemId) == 0) {
			doesNotHaveItem.execute(gameState);
		} else {
			hasItem.execute(gameState);
		}
	}
}