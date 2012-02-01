/*
 * Created on Oct 9, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.Closure;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestItemClosure extends AbstractCommand {

	private int itemId = -1;
	private Closure hasItem;
	private Closure doesNotHaveItem;

	public TestItemClosure(Element element) {
		this.executeSuccessful = true;
		this.itemId = Integer.parseInt(element.getAttribute("id"));
		this.hasItem = AbstractCommandLoader.loadClosureFromChildTag(element, "successful");
		this.doesNotHaveItem = AbstractCommandLoader.loadClosureFromChildTag(element, "unsuccessful");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(GameState gameState) {
		if (gameState.getPlayerState().itemCount(this.itemId) == 0) {
			this.doesNotHaveItem.execute(gameState);
		} else {
			this.hasItem.execute(gameState);
		}
	}
}