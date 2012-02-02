package rustleund.fightingfantasy.framework;

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
