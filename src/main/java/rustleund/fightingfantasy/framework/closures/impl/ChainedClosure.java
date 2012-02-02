package rustleund.fightingfantasy.framework.closures.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rustleund.fightingfantasy.framework.base.GameState;
import rustleund.fightingfantasy.framework.closures.Closure;

public class ChainedClosure implements Closure {

	private List<Closure> closures;

	public ChainedClosure() {
		this.closures = new ArrayList<Closure>();
	}

	public ChainedClosure(List<Closure> closures) {
		this.closures = closures;
	}

	public ChainedClosure(Closure... closures) {
		this.closures = new ArrayList<Closure>(Arrays.asList(closures));
	}

	public void addClosure(Closure closure) {
		this.closures.add(closure);
	}

	@Override
	public boolean execute(GameState gameState) {
		if (this.closures != null) {
			for (Closure closure : this.closures) {
				if (!closure.execute(gameState)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return this.closures.toString();
	}

}
