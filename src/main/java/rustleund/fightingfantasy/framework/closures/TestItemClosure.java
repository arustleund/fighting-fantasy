/*
 * Created on Oct 9, 2005
 */
package rustleund.fightingfantasy.framework.closures;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.AbstractCommand;
import rustleund.fightingfantasy.framework.Closure;
import rustleund.fightingfantasy.framework.GameState;
import rustleund.fightingfantasy.framework.util.AbstractCommandLoader;

/**
 * @author rustlea
 */
public class TestItemClosure extends AbstractCommand {

	private int itemId = -1;
	private Closure hasItem;
	private Closure doesNotHaveItem;

	public TestItemClosure(Element element) {
		this.itemId = Integer.parseInt(element.getAttribute("id"));
		this.hasItem = AbstractCommandLoader.loadClosureFromChildTag(element, "successful");
		this.doesNotHaveItem = AbstractCommandLoader.loadClosureFromChildTag(element, "unsuccessful");
	}

	@Override
	public boolean execute(GameState gameState) {
		if (gameState.getPlayerState().itemCount(this.itemId) == 0) {
			return this.doesNotHaveItem.execute(gameState);
		}
		return this.hasItem.execute(gameState);
	}
}