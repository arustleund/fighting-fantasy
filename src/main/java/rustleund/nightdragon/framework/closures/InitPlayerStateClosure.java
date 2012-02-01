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

	public InitPlayerStateClosure(Element element) {
		this.executeSuccessful = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(Object arg0) {
		GameState gameState = (GameState) arg0;
		gameState.setPlayerState(new PlayerState(gameState.getPlayerState().getName()));
	}

}