/*
 * Created on Jul 5, 2004
 */
package rustleund.fightingfantasy.framework.closures;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.AbstractCommand;
import rustleund.fightingfantasy.framework.GameState;

/**
 * @author rustlea
 */
public class DisplayTextClosure extends AbstractCommand {

	private int textId;

	public DisplayTextClosure(Element element) {
		this.textId = Integer.valueOf(element.getAttribute("id"));
		this.executeSuccessful = true;
	}

	public DisplayTextClosure(int textId) {
		this.textId = textId;
		this.executeSuccessful = true;
	}

	public void execute(GameState gameState) {
		String text = gameState.getPageState().getTexts().get(this.textId);
		gameState.getPageState().addToPagetext(text);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}