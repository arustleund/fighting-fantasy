/*
 * Created on Oct 9, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;

/**
 * @author rustlea
 */
public class RemoveItemClosure extends AbstractClosure {

	private int itemId = 0;

	public RemoveItemClosure(Element element) {
		this.itemId = Integer.parseInt(element.getAttribute("id"));
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.getPlayerState().removeOneOfItem(itemId);
		return true;
	}

}