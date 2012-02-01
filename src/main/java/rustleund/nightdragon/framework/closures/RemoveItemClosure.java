/*
 * Created on Oct 9, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;

/**
 * @author rustlea
 */
public class RemoveItemClosure extends AbstractCommand {

	private int itemId = 0;

	public RemoveItemClosure(Element element) {

		this.executeSuccessful = true;

		this.itemId = Integer.parseInt(element.getAttribute("id"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(GameState gameState) {

		gameState.getPlayerState().removeOneOfItem(itemId);
	}

}