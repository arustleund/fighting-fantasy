/*
 * Created on Jul 5, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;

/**
 * @author rustlea
 */
public class DisplayTextClosure extends AbstractClosure {

	private int textId;

	public DisplayTextClosure(Element element) {
		this.textId = Integer.parseInt(element.getAttribute("id"));
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
}