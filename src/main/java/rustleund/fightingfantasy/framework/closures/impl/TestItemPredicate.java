package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;

import com.google.common.base.Predicate;

public class TestItemPredicate implements Predicate<GameState> {

	private int itemId;

	/**
	 * @param A {@code <testItem />} {@link Element} with an <code>id</code> attribute containing an item id
	 */
	public TestItemPredicate(Element element) {
		this.itemId = Integer.parseInt(element.getAttribute("id"));
	}

	@Override
	public boolean apply(GameState input) {
		return input.getPlayerState().itemCount(itemId) > 0;
	}

}
