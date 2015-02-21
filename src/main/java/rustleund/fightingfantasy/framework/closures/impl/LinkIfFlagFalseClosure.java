package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.BattleEffectsLoader;
import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

public class LinkIfFlagFalseClosure implements Closure {

	private TestFlagPredicate testFlagPredicate;
	private LinkClosure linkClosure;

	public LinkIfFlagFalseClosure(Element element, ClosureLoader closureLoader, BattleEffectsLoader battleEffectsLoader) {
		this.testFlagPredicate = new TestFlagPredicate(element);
		this.linkClosure = new LinkClosure(element, closureLoader, battleEffectsLoader);
	}

	@Override
	public boolean execute(GameState gameState) {
		return this.testFlagPredicate.apply(gameState) ? true : this.linkClosure.execute(gameState);
	}
}
