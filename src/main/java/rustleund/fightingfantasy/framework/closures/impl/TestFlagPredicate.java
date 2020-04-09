package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;

import com.google.common.base.Predicate;

public class TestFlagPredicate implements Predicate<GameState> {

	private int flagId;

	/**
	 * @param An {@link Element} that represents the {@code <testFlag />} {@link Element}. Must contain an <code>id</code> attribute with a valid integer representing a flag id.
	 */
	public TestFlagPredicate(Element element) {
		this.flagId = Integer.parseInt(element.getAttribute("id"));
	}

	public int getFlagId() {
		return flagId;
	}

	@Override
	public boolean apply(GameState input) {
		return input.getPlayerState().getFlagValue(flagId);
	}

}
