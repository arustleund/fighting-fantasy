package rustleund.fightingfantasy.framework.closures.impl;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

import com.google.common.base.Predicate;

public class TestClosure implements Closure {

	private Predicate<? super GameState> predicate;
	private Closure trueClosure;
	private Closure falseClosure;

	public TestClosure(Predicate<? super GameState> predicate, Closure trueClosure, Closure falseClosure) {
		this.predicate = predicate;
		this.trueClosure = trueClosure;
		this.falseClosure = falseClosure;
	}

	@Override
	public boolean execute(GameState gameState) {
		if (this.predicate.apply(gameState)) {
			return this.trueClosure.execute(gameState);
		}
		return this.falseClosure.execute(gameState);
	}

}
