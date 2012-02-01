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
		this.itemId = Integer.parseInt(element.getAttribute("id"));
	}

	public boolean execute(GameState gameState) {
		gameState.getPlayerState().removeOneOfItem(itemId);
		return true;
	}

}