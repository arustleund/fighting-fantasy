package rustleund.fightingfantasy.framework.closures;

import rustleund.fightingfantasy.framework.base.GameState;

public interface Closure {

	/**
	 * Execute a game task.
	 * 
	 * @param gameState The current {@link GameState}
	 * 
	 * @return <code>true</code> if the execution was successful, <code>false</code> otherwise
	 */
	boolean execute(GameState gameState);

}
