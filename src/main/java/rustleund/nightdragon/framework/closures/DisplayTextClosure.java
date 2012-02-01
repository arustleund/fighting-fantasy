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
		this.executeSuccessful = true;
	}

	public DisplayTextClosure(int textId) {
		this.textId = textId;
		this.executeSuccessful = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(Object object) {

		GameState gameState = (GameState) object;

		String text = (String) gameState.getPageState().getTexts().get(new Integer(textId));

		gameState.getPageState().addToPagetext(text);

	}

}