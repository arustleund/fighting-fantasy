/*
 * Created on Jul 5, 2004
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

/**
 * @author rustlea
 */
public class DisplayTextClosure implements Closure {

	private final int textId;

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