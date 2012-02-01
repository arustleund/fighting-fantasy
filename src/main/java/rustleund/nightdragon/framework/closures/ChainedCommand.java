package rustleund.nightdragon.framework.closures;

import java.util.List;

import rustleund.nightdragon.framework.Command;
import rustleund.nightdragon.framework.GameState;

public class ChainedCommand implements Command {

	private List<Command> commands;

	public ChainedCommand(List<Command> commands) {
		this.commands = commands;
	}

	@Override
	public void execute(GameState gameState) {
		for (Command command : commands) {
			command.execute(gameState);
		}
	}

}
