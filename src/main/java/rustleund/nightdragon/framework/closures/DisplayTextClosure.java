/*
 * Created on Jul 5, 2004
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;

/**
 * @author rustlea
 */
public class DisplayTextClosure extends AbstractCommand {

	private int textId;

	public DisplayTextClosure(Element element) {
		this.textId = Integer.parseInt(element.getAttribute("id"));
	}

	public DisplayTextClosure(int textId) {
		this.textId = textId;
	}

	public boolean execute(GameState gameState) {
		String text = gameState.getPageState().getTexts().get(textId);
		gameState.getPageState().addToPagetext(text);
		return true;
	}

}