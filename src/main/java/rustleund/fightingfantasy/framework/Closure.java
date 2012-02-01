package rustleund.fightingfantasy.framework;

public interface Closure {

	void execute(GameState gameState);
	
	boolean executeWasSuccessful();
	
}
