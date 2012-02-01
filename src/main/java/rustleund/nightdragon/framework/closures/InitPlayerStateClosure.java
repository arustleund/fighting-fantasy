/*
 * Created on Oct 25, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;

/**
 * @author rustlea
 */
public class InitPlayerStateClosure extends AbstractCommand {

	public InitPlayerStateClosure(@SuppressWarnings("unused") Element element) {
		this.executeSuccessful = true;
	}

	@Override
	public void execute(GameState gameState) {
		gameState.setPlayerState(new PlayerState(gameState.getPlayerState().getName()));
	}

}