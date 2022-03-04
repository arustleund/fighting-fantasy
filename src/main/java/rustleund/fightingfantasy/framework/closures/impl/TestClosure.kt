package rustleund.fightingfantasy.framework.closures.impl;

import org.w3c.dom.Element;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;
import rustleund.fightingfantasy.framework.closures.ClosureLoader;

import java.util.function.Predicate;

public class TestClosure implements Closure {

	private final Predicate<? super GameState> predicate;
	private final Closure trueClosure;
	private final Closure falseClosure;

	/**
	 * @param predicate The {@link Predicate} to use to determine which {@link Closure} to executre
	 * @param closureLoader The {@link ClosureLoader} to use for loading the true and false {@link Closure}s
	 * @param element The {@link Element} that represents the main test {@link Element}, should have one each of {@code <successful />} and {@code <unsuccessful />} child elements
	 */
	public TestClosure(Predicate<? super GameState> predicate, ClosureLoader closureLoader, Element element) {
		this.predicate = predicate;
		this.trueClosure = closureLoader.loadClosureFromChild(element, "successful");
		this.falseClosure = closureLoader.loadClosureFromChild(element, "unsuccessful");
	}

	@Override
	public boolean execute(GameState gameState) {
		if (this.predicate.test(gameState)) {
			return this.trueClosure.execute(gameState);
		}
		return this.falseClosure.execute(gameState);
	}

}
