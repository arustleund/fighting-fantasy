package rustleund.nightdragon.framework;

public interface Closure {

	void execute(GameState gameState);
	
	boolean executeWasSuccessful();
	
}
