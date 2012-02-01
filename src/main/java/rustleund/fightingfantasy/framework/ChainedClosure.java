package rustleund.fightingfantasy.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ChainedClosure implements Closure {

	private List<Closure> closures;
	private boolean executeSuccessful = true;

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

	public void execute(GameState gameState) {
		if (this.closures != null) {
			Iterator<Closure> closureIter = this.closures.iterator();
			while (this.executeSuccessful && closureIter.hasNext()) {
				Closure closure = closureIter.next();
				closure.execute(gameState);
				this.executeSuccessful = closure.executeWasSuccessful();
			}
		}
	}

	public boolean executeWasSuccessful() {
		return this.executeSuccessful;
	}

	public String toString() {
		return this.closures.toString();
	}

}
