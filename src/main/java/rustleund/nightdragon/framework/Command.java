package rustleund.nightdragon.framework;

public interface Command {

	void execute(GameState gameState);

	boolean executeWasSuccessful();

}
