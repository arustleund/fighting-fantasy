package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;

import java.util.function.Predicate;

public class TestItemPredicate implements Predicate<GameState> {

	private final Predicate<GameState> delegate;

	/**
	 * @param element A {@code <testItem />} {@link Element} with an <code>id</code> attribute containing an item id
	 */
	public TestItemPredicate(Element element) {
		int itemId = Integer.parseInt(element.getAttribute("id"));
		this.delegate = new TestNumberPredicate(element, gameState -> gameState.getPlayerState().itemCount(itemId));
	}

	@Override
	public boolean test(GameState input) {
		return this.delegate.test(input);
	}
}
