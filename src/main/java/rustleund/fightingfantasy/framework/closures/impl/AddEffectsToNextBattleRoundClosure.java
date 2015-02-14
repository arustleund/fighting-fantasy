package rustleund.fightingfantasy.framework.closures.impl;

import java.util.Collections;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class AddEffectsToNextBattleRoundClosure implements Closure {

	private Closure closure;

	public AddEffectsToNextBattleRoundClosure(Element element, ClosureLoader closureLoader) {
		this.closure = closureLoader.loadClosureFromChildren(element);
	}

	@Override
	public boolean execute(GameState gameState) {
		gameState.getBattleState().addEffectsForNextRound(Collections.singleton(closure));
		return true;
	}
}
