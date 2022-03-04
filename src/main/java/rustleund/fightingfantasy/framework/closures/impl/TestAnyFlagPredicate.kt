package rustleund.fightingfantasy.framework.closures.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.base.PlayerState;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class TestAnyFlagPredicate implements Predicate<GameState> {

	private List<Integer> flagIds;

	/**
	 * Main constructor
	 * 
	 * @param An {@link Element} that represents an {@code<anyFlag />} element. Must contain an <code>ids</code> attribute with comma-separated integers representing flag ids.
	 */
	public TestAnyFlagPredicate(Element element) {
		this.flagIds = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(element.getAttribute("ids"), ",");
		while (tokenizer.hasMoreTokens()) {
			this.flagIds.add(Integer.valueOf(tokenizer.nextToken()));
		}
	}

	@Override
	public boolean apply(GameState gameState) {
		final PlayerState playerState = gameState.getPlayerState();
		return Iterables.any(this.flagIds, new Predicate<Integer>() {
			@Override
			public boolean apply(Integer input) {
				return playerState.getFlagValue(input);
			}
		});
	}

}
