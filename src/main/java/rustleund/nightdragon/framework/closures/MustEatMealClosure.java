/*
 * Created on Oct 27, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;

/**
 * @author rustlea
 */
public class MustEatMealClosure extends AbstractCommand {

	private int number = 1;

	public MustEatMealClosure(Element element) {
		if (element.hasAttribute("amount")) {
			this.number = Integer.parseInt(element.getAttribute("amount"));
		}
	}

	public void execute(GameState gameState) {
		PlayerState playerState = gameState.getPlayerState();

		for (int i = 0; i < this.number; i++) {
			this.executeSuccessful = true;
			if (playerState.getProvisions().getCurrentValue() > 0) {
				playerState.getProvisions().adjustCurrentValueNoException(-1);
			} else {
				playerState.getStamina().adjustCurrentValueNoException(-2);
				if (playerState.isDead()) {
					this.executeSuccessful = false;
					new LinkClosure("0").execute(gameState);
				}
			}
		}
	}

}