/*
 * Created on Oct 10, 2005
 */
package rustleund.nightdragon.framework.closures;

import org.apache.commons.collections.Closure;
import org.w3c.dom.Element;

import rustleund.nightdragon.framework.AbstractCommand;
import rustleund.nightdragon.framework.GameState;
import rustleund.nightdragon.framework.PlayerState;
import rustleund.nightdragon.framework.util.AbstractCommandLoader;
import rustleund.nightdragon.framework.util.DiceRoller;

/**
 * @author rustlea
 */
public class TestSkillClosure extends AbstractCommand {

	private int diceRollAdjustment = 0;

	private Closure successful = null;

	private Closure unsuccessful = null;

	public TestSkillClosure(Element element) {
		if (element.hasAttribute("diceRollAdjustment")) {
			this.diceRollAdjustment = Integer.parseInt(element.getAttribute("diceRollAdjustment"));
		}

		this.successful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("successful").item(0));
		this.unsuccessful = AbstractCommandLoader.loadChainedClosure((Element) element.getElementsByTagName("unsuccessful").item(0));

		this.executeSuccessful = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.collections.Closure#execute(java.lang.Object)
	 */
	public void execute(Object arg0) {
		GameState gameState = (GameState) arg0;

		PlayerState playerState = gameState.getPlayerState();

		if ((DiceRoller.rollDice(2) + diceRollAdjustment) <= playerState.getSkill().getCurrentValue()) {
			successful.execute(gameState);
		} else {
			unsuccessful.execute(gameState);
		}
	}

}