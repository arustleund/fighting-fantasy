/*
 * Created on Oct 9, 2005
 */
package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

/**
 * @author rustlea
 */
public class TestItemClosure extends AbstractClosure {

	private int itemId = -1;
	private Closure hasItem;
	private Closure doesNotHaveItem;

	public TestItemClosure(Element element) {
		this.itemId = Integer.parseInt(element.getAttribute("id"));
		this.hasItem = DefaultClosureLoader.loadClosureFromChildTag(element, "successful");
		this.doesNotHaveItem = DefaultClosureLoader.loadClosureFromChildTag(element, "unsuccessful");
	}

	@Override
	public boolean execute(GameState gameState) {
		if (gameState.getPlayerState().itemCount(this.itemId) == 0) {
			return this.doesNotHaveItem.execute(gameState);
		}
		return this.hasItem.execute(gameState);
	}
}