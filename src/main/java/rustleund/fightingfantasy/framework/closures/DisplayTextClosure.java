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
	}

	public DisplayTextClosure(int textId) {
		this.textId = textId;
	}

	@Override
	public boolean execute(GameState gameState) {
		String text = gameState.getPageState().getTexts().get(this.textId);
		gameState.getPageState().addToPagetext(text);
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}